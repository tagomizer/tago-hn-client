-- MISSING some indexes, and GRANTs
-- From Marc-Antoine
CREATE EXTENSION IF NOT EXISTS ltree WITH SCHEMA public;
CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA public;
SET ROLE tq_admin;

CREATE SEQUENCE IF NOT EXISTS public.topic_id_seq
    AS bigint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- TODO HN stories can be TELL, ASK, SHOW, etc.
-- that shows up in the title
-- From https://github.com/HackerNews/API
-- types are: "job", "story", "comment", "poll", or "pollopt"
DROP TYPE IF EXISTS public.post_type CASCADE;
CREATE TYPE public.post_type AS ENUM (
    'story',
    'comment',
    'job',
    'poll',
    'pollopt'
    'tag'
);

DROP TYPE IF EXISTS public.external_service CASCADE;
CREATE TYPE public.external_service AS ENUM (
  'hackernews',
  'slack',
  'mattermost',
  'hypothesis'
);

-- adding tags
-- tags are associated with users and posts
-- hackernews does not have tags, but other platforms, e.g. hypothesis, do
CREATE TABLE IF NOT EXISTS public.tag (
  id BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('public.topic_id_seq'::regclass),
  tag TEXT NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS tag_idx ON public.tag (tag);



-- index title, text id

CREATE TABLE IF NOT EXISTS public.author (
  id BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('public.topic_id_seq'::regclass),
  -- name if known?
  text TEXT NOT NULL
);


CREATE UNIQUE INDEX IF NOT EXISTS author_handle_idx ON public.author (text);

CREATE TABLE IF NOT EXISTS public.tag_author (
	author_id BIGINT NOT NULl REFERENCES  public.author (id),
	tag_id BIGINT NOT NULl REFERENCES  public.tag (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS tag_author_idx ON public.tag_author (author_id, tag_id);


-- NOTE some URLs do not belong to an post - they are just referenced in text
CREATE TABLE IF NOT EXISTS public.url (
  	id BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('public.topic_id_seq'::regclass),
  	url	TEXT NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS url_idx ON public.url (url);


-- story and comment kids are found by joins
-- note ALSO that transclusions would have to be treated separately.
-- note that the same story might be posted several times, e.g. same url, etc which safe to do
-- because each event will have its own id
CREATE TABLE IF NOT EXISTS public.post (
  	id BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('public.topic_id_seq'::regclass),
  	post_type 	TEXT NOT NULL,
  	ext_source	TEXT NOT NULL DEFAULT 'hackernews',
  	ext_id			varchar NOT NULL, -- the object id for e.g. Hackernews objects
  	author 			BIGINT REFERENCES public.author(id),   -- may not need
    story_url   BIGINT REFERENCES public.url(id),
  	pub_time	TIMESTAMP WITHOUT TIME ZONE,
  	mod_time	TIMESTAMP WITHOUT TIME ZONE,
  	score			int DEFAULT 0, -- Score can change over time
  	descendents	int DEFAULT 0, -- number of child nodes
  	lang			varchar(12),  -- allows to store variants such as zh_Hant_TW
  	is_dead		BOOLEAN DEFAULT false, -- found in the HN api fields
  	is_deleted	BOOLEAN DEFAULT false, -- found in the HN api fields
  	-- note: the HN API has a field called "poll" which is a type of post
  	-- note: the HN API has a field called "parts" which refers to parts of a poll
  	-- parts are just like kids which means we need a parts table
  	title			TEXT,
  	body			TEXT,
  	parent			BIGINT REFERENCES public.post(id),
    ancestry ltree DEFAULT ''::ltree
);


CREATE OR REPLACE FUNCTION public.update_post_ancestry(post_id BIGINT, _ancestry ltree) RETURNS void
  LANGUAGE plpgsql
  AS $$
DECLARE _parent_id BIGINT;
DECLARE row record;
BEGIN
  IF _ancestry IS NULL THEN
    SELECT parent INTO _parent_id FROM public.post WHERE id = post_id;
    IF _parent_id IS NULL THEN
      _ancestry := ''::ltree;
    ELSE
      SELECT ancestry INTO STRICT _ancestry FROM public.post WHERE id = _parent_id;
    END IF;
  END IF;
  _ancestry := _ancestry || post_id::varchar::ltree;
  UPDATE public.post SET ancestry = _ancestry WHERE id = post_id;
  FOR row IN SELECT id FROM public.post WHERE parent = post_id
  LOOP
    PERFORM public.update_post_ancestry(row.id, _ancestry);
  END LOOP;
END$$;

CREATE OR REPLACE FUNCTION public.after_update_post() RETURNS trigger
  LANGUAGE plpgsql
  AS $$
DECLARE parent_ancestry ltree;
BEGIN
  CASE WHEN NEW.id != OLD.id THEN
    IF NEW.parent IS NULL THEN
      PERFORM public.update_post_ancestry(NEW.id, NULL);
    ELSE
      PERFORM public.update_post_ancestry(NEW.parent, NULL);
    END IF;
  WHEN NEW.parent != OLD.parent THEN
    PERFORM public.update_post_ancestry(NEW.id, NULL);
  ELSE
  END CASE;
  RETURN NEW;
END$$;
DROP TRIGGER IF EXISTS after_update_post ON public.post;
CREATE TRIGGER after_update_post AFTER UPDATE ON public.post FOR EACH ROW EXECUTE FUNCTION public.after_update_post();

CREATE OR REPLACE FUNCTION public.after_create_post() RETURNS trigger
  LANGUAGE plpgsql
  AS $$
BEGIN
  PERFORM public.update_post_ancestry(NEW.id, NULL);
  RETURN NEW;
END$$;
DROP TRIGGER IF EXISTS before_create_post ON public.post;
DROP TRIGGER IF EXISTS after_create_post ON public.post;
DROP TRIGGER IF EXISTS after_create_post_t ON public.post;
CREATE TRIGGER after_create_post_t AFTER INSERT ON public.post FOR EACH ROW EXECUTE FUNCTION public.after_create_post();


CREATE INDEX IF NOT EXISTS post_ancestry_gist_idx ON public.post USING GIST (ancestry);
CREATE UNIQUE INDEX IF NOT EXISTS post_external_identity ON public.post (ext_source, ext_id);
CREATE INDEX IF NOT EXISTS post_title_en_idx on public.post using gin (to_tsvector('english', title)) WHERE starts_with(lang, 'en');
CREATE INDEX  IF NOT EXISTS post_whole_en_idx on public.post using gin ((to_tsvector('english', title) || to_tsvector('english', body))) WHERE starts_with(lang, 'en');

CREATE TABLE IF NOT EXISTS public.tag_post (
	post_id BIGINT NOT NULl REFERENCES  public.post (id),
	tag_id BIGINT NOT NULl REFERENCES  public.tag (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS tag_post_idx ON public.tag_post (post_id, tag_id);

DROP TYPE IF EXISTS public.embedding_name_type CASCADE;
CREATE TYPE public.embedding_name_type AS ENUM (
  'universal_sentence_encoder_4'
);

CREATE TABLE IF NOT EXISTS public.embedding (
    id bigint GENERATED ALWAYS AS IDENTITY,
    embedding_name embedding_name_type DEFAULT 'universal_sentence_encoder_4',
    doc_id bigint,
    fragment_id bigint,
    embedding vector(512) NOT NULL,
    CONSTRAINT embedding_doc_id_key FOREIGN KEY (doc_id)
      REFERENCES public.post (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX IF NOT EXISTS embedding_doc_id_idx on public.embedding (doc_id);
CREATE INDEX IF NOT EXISTS embedding_cosidx ON embedding USING ivfflat (embedding vector_cosine_ops);
