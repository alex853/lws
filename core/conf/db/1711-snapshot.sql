
CREATE SEQUENCE is_scenery_id_seq;

CREATE TABLE is_scenery (
    id integer DEFAULT nextval('is_scenery_id_seq'::regclass) NOT NULL CONSTRAINT pk_is_scenery PRIMARY KEY,
    version smallint NOT NULL,
    create_dt timestamp without time zone NOT NULL,
    modify_dt timestamp without time zone NOT NULL,

    title character varying(100),
    description character varying(1000),
    authors character varying(100)
);





CREATE SEQUENCE is_revision_id_seq;

CREATE TABLE is_revision (
    id integer DEFAULT nextval('is_revision_id_seq'::regclass) NOT NULL CONSTRAINT pk_is_revision PRIMARY KEY,
    version smallint NOT NULL,
    create_dt timestamp without time zone NOT NULL,
    modify_dt timestamp without time zone NOT NULL,

    scenery_id integer NOT NULL CONSTRAINT fk_is_revision_scenery_id REFERENCES is_scenery(id),
    rev_number integer NOT NULL,
    status smallint NOT NULL,
    comment character varying(200),
    images character varying(500),
    repo_path character varying(100),
    repo_mode integer,
    dest_path character varying(100),
    installation_steps character varying(4000),

    CONSTRAINT uq_is_revision_scenery_id_rev_number UNIQUE (scenery_id, rev_number)
);





CREATE SEQUENCE is_object_id_seq;

CREATE TABLE is_object (
    id integer DEFAULT nextval('is_object_id_seq'::regclass) NOT NULL CONSTRAINT pk_is_object PRIMARY KEY,
    version smallint NOT NULL,
    create_dt timestamp without time zone NOT NULL,
    modify_dt timestamp without time zone NOT NULL,

    revision_id integer NOT NULL CONSTRAINT fk_is_object_revision_id REFERENCES is_revision(id),
    latitude real,
    longitude real,
    type smallint NOT NULL,
    airport_id integer CONSTRAINT fk_is_object_airport_id REFERENCES refdata_airport(id),
    images character varying(500)
);
