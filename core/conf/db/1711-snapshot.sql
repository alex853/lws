
CREATE SEQUENCE lws_scenery_id_seq;

CREATE TABLE lws_scenery (
    id integer DEFAULT nextval('lws_scenery_id_seq'::regclass) NOT NULL CONSTRAINT pk_lws_scenery PRIMARY KEY,
    version smallint NOT NULL,
    create_dt timestamp without time zone NOT NULL,
    modify_dt timestamp without time zone NOT NULL,

    title character varying(100),
    description character varying(1000),
    authors character varying(100)
);





CREATE SEQUENCE lws_revision_id_seq;

CREATE TABLE lws_revision (
    id integer DEFAULT nextval('lws_revision_id_seq'::regclass) NOT NULL CONSTRAINT pk_lws_revision PRIMARY KEY,
    version smallint NOT NULL,
    create_dt timestamp without time zone NOT NULL,
    modify_dt timestamp without time zone NOT NULL,

    scenery_id integer NOT NULL CONSTRAINT fk_lws_revision_scenery_id REFERENCES lws_scenery(id),
    rev_number integer NOT NULL,
    status smallint NOT NULL,
    comment character varying(200),
    images character varying(500),
    repo_path character varying(100),
    repo_mode integer,
    dest_path character varying(100),
    installation_steps character varying(4000),

    CONSTRAINT uq_lws_revision_scenery_id_rev_number UNIQUE (scenery_id, rev_number)
);





CREATE SEQUENCE lws_object_id_seq;

CREATE TABLE lws_object (
    id integer DEFAULT nextval('lws_object_id_seq'::regclass) NOT NULL CONSTRAINT pk_lws_object PRIMARY KEY,
    version smallint NOT NULL,
    create_dt timestamp without time zone NOT NULL,
    modify_dt timestamp without time zone NOT NULL,

    revision_id integer NOT NULL CONSTRAINT fk_lws_object_revision_id REFERENCES lws_revision(id),
    latitude real,
    longitude real,
    type smallint NOT NULL,
    airport_id integer CONSTRAINT fk_lws_object_airport_id REFERENCES refdata_airport(id),
    images character varying(500)
);
