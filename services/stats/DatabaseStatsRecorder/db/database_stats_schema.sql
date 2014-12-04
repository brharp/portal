DROP TABLE up_stats_host CASCADE;
DROP TABLE up_stats_channel_event CASCADE;
DROP TABLE up_stats_channel_event_type CASCADE;
DROP TABLE up_stats_folder_event CASCADE;
DROP TABLE up_stats_folder_event_type CASCADE;
DROP TABLE up_stats_user_event CASCADE;
DROP TABLE up_stats_user_event_type CASCADE;

CREATE TABLE up_stats_host
(
  id int2 NOT NULL,
  name varchar(20),
  "desc" varchar(80)
) 
WITHOUT OIDS;
CREATE UNIQUE INDEX up_stats_host_index ON up_stats_host USING btree (id);
INSERT INTO up_stats_host (id, name, "desc") VALUES (0, 'UNSP', 'unspecified host');
INSERT INTO up_stats_host (id, name, "desc") VALUES (1, 'P1', 'happy');
INSERT INTO up_stats_host (id, name, "desc") VALUES (2, 'P2', 'doc');
INSERT INTO up_stats_host (id, name, "desc") VALUES (3, 'P3', 'bashful');
INSERT INTO up_stats_host (id, name, "desc") VALUES (4, 'P4', 'sneezy');
INSERT INTO up_stats_host (id, name, "desc") VALUES (5, 'P5', 'grumpy');
INSERT INTO up_stats_host (id, name, "desc") VALUES (6, 'QA1', 'sleepy');
INSERT INTO up_stats_host (id, name, "desc") VALUES (7, 'QA2', 'dopey');
INSERT INTO up_stats_host (id, name, "desc") VALUES (8, 'L1', 'snowwhite');
INSERT INTO up_stats_host (id, name, "desc") VALUES (9, 'DEV1', 'dragonfly');

CREATE TABLE up_stats_channel_event
(
  type_id integer NOT NULL,
  user_id integer NOT NULL,
  chan_id integer NOT NULL,
  host_id int2,
  ip_address varchar(32),
  "time" timestamp
) 
WITHOUT OIDS;
CREATE INDEX up_stats_channel_event_index ON up_stats_channel_event USING btree (type_id, user_id);

CREATE TABLE up_stats_channel_event_type
(
  id int2 NOT NULL,
  name varchar(30),
  "desc" varchar(200)
) 
WITHOUT OIDS;
CREATE UNIQUE INDEX up_stats_channel_event_type_index ON up_stats_channel_event_type USING btree (id);
ALTER TABLE ONLY up_stats_channel_event_type
    ADD CONSTRAINT up_stats_channel_event_type_pkey PRIMARY KEY (id);

INSERT INTO up_stats_channel_event_type (id, name, "desc") VALUES (1, 'ChannelDefinitionPublished', 'A new channel published via Channel Manager''s "Publish a new channel"');
INSERT INTO up_stats_channel_event_type (id, name, "desc") VALUES (2, 'ChannelDefinitionModified', 'A channel modified via Channel Manager''s "Modify a currently published channel"');
INSERT INTO up_stats_channel_event_type (id, name, "desc") VALUES (3, 'ChannelDefinitionRemoved', 'A channel removed via Channel Manager''s "Modify a currently published channel"');
INSERT INTO up_stats_channel_event_type (id, name, "desc") VALUES (4, 'ChannelAddedToLayout', 'A channel added to a user''s layout via "preferences"');
INSERT INTO up_stats_channel_event_type (id, name, "desc") VALUES (5, 'ChannelUpdatedInLayout', NULL);
INSERT INTO up_stats_channel_event_type (id, name, "desc") VALUES (6, 'ChannelMovedInLayout', 'A channel was moved within a user''s layout');
INSERT INTO up_stats_channel_event_type (id, name, "desc") VALUES (7, 'ChannelRemovedFromLayout', 'A channel was removed from a user''s layout');
INSERT INTO up_stats_channel_event_type (id, name, "desc") VALUES (8, 'ChannelInstantiated', 'A channel was instantiated in the user''s session for the first time (if not cached)');
INSERT INTO up_stats_channel_event_type (id, name, "desc") VALUES (9, 'ChannelRendered', 'A channel was rendered on the user''s browser');
INSERT INTO up_stats_channel_event_type (id, name, "desc") VALUES (10, 'ChannelTargeted', NULL);

--
--
--

CREATE TABLE up_stats_folder_event
(
  type_id integer NOT NULL,
  user_id integer NOT NULL,
  folder_id integer NOT NULL,
  folder_name varchar(200),
  host_id int2,
  ip_address varchar(32),
  "time" timestamp
) 
WITHOUT OIDS;
CREATE INDEX up_stats_folder_event_index ON up_stats_folder_event USING btree (type_id, user_id);

CREATE TABLE up_stats_folder_event_type
(
  id int2 NOT NULL,
  name varchar(30),
  "desc" varchar(200)
) 
WITHOUT OIDS;
CREATE UNIQUE INDEX up_stats_folder_event_type_index ON up_stats_folder_event_type USING btree (id);
ALTER TABLE ONLY up_stats_folder_event_type
    ADD CONSTRAINT up_stats_folder_event_type_pkey PRIMARY KEY (id);

INSERT INTO up_stats_folder_event_type (id, name, "desc") VALUES (1, 'FolderAddedToLayout', 'A new column or tab was created in a user''s layout');
INSERT INTO up_stats_folder_event_type (id, name, "desc") VALUES (2, 'FolderUpdatedInLayout', 'A column or tab was updated in a user''s layout (typically a tab, when renamed)');
INSERT INTO up_stats_folder_event_type (id, name, "desc") VALUES (3, 'FolderMovedInLayout', 'A column or tab was moved in a user''s layout');
INSERT INTO up_stats_folder_event_type (id, name, "desc") VALUES (4, 'FolderRemovedFromLayout', 'A column or tab was removed from a user''s layout');

--
--
--

CREATE TABLE up_stats_user_event
(
  type_id integer NOT NULL,
  user_id integer NOT NULL,
  host_id int2,
  ip_address varchar(32),
  "time" timestamp
) 
WITHOUT OIDS;
CREATE INDEX up_stats_user_event_index ON up_stats_user_event USING btree (type_id, user_id);

CREATE TABLE up_stats_user_event_type
(
  id int2 NOT NULL,
  name varchar(30),
  "desc" varchar(200)
) 
WITHOUT OIDS;
CREATE UNIQUE INDEX up_suser_user_event_type_index ON up_stats_user_event_type USING btree (id);
ALTER TABLE ONLY up_stats_user_event_type
    ADD CONSTRAINT up_stats_user_event_type_pkey PRIMARY KEY (id);

INSERT INTO up_stats_user_event_type (id, name, "desc") VALUES (1, 'Login', 'A user logging in');
INSERT INTO up_stats_user_event_type (id, name, "desc") VALUES (2, 'Logout', 'A user logging out');
INSERT INTO up_stats_user_event_type (id, name, "desc") VALUES (3, 'SessionCreated', 'A portal session being created on the server');
INSERT INTO up_stats_user_event_type (id, name, "desc") VALUES (4, 'SessionDestroyed', 'A portal session being destroyed on the server');

--
-- Establish foreign key relations
--
ALTER TABLE ONLY up_stats_channel_event
    ADD CONSTRAINT chan_id_fkey FOREIGN KEY (chan_id) REFERENCES up_channel (chan_id) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE ONLY up_stats_channel_event
    ADD CONSTRAINT host_id_fkey FOREIGN KEY (host_id) REFERENCES up_stats_host (id) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE ONLY up_stats_channel_event
    ADD CONSTRAINT type_id_fkey FOREIGN KEY (type_id) REFERENCES up_stats_channel_event_type (id) ON UPDATE RESTRICT ON DELETE RESTRICT;
--ALTER TABLE ONLY up_stats_channel_event
--    ADD CONSTRAINT user_id_fkey FOREIGN KEY (user_id) REFERENCES up_user (user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;
    
ALTER TABLE ONLY up_stats_folder_event
    ADD CONSTRAINT host_id_fkey FOREIGN KEY (host_id) REFERENCES up_stats_host (id) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE ONLY up_stats_folder_event
    ADD CONSTRAINT type_id_fkey FOREIGN KEY (type_id) REFERENCES up_stats_folder_event_type (id) ON UPDATE RESTRICT ON DELETE RESTRICT;
--ALTER TABLE ONLY up_stats_folder_event
--    ADD CONSTRAINT user_id_fkey FOREIGN KEY (user_id) REFERENCES up_user (user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;
    
ALTER TABLE ONLY up_stats_user_event
    ADD CONSTRAINT host_id_fkey FOREIGN KEY (host_id) REFERENCES up_stats_host (id) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE ONLY up_stats_user_event
    ADD CONSTRAINT type_id_fkey FOREIGN KEY (type_id) REFERENCES up_stats_user_event_type (id) ON UPDATE RESTRICT ON DELETE RESTRICT;
--ALTER TABLE ONLY up_stats_user_event
--    ADD CONSTRAINT user_id_fkey FOREIGN KEY (user_id) REFERENCES up_user (user_id) ON UPDATE RESTRICT ON DELETE RESTRICT;
    
