SELECT
ush.name as host,
usce.time,
uu.user_name as user,
uscet.name as event,
uc.chan_name as channel
FROM
up_stats_channel_event usce,
up_stats_channel_event_type uscet,
up_channel uc,
up_stats_host ush,
up_user uu
WHERE
usce.type_id=uscet.id AND 
usce.chan_id=uc.chan_id AND 
usce.host_id=ush.id AND 
usce.user_id=uu.user_id 
ORDER BY
usce.time DESC
;

SELECT
ush.name as host,
usue.time,
uu.user_name as user,
usuet.name as event
FROM
up_stats_user_event usue,
up_stats_user_event_type usuet,
up_stats_host ush,
up_user uu
WHERE
usue.type_id=usuet.id AND 
usue.host_id=ush.id AND 
usue.user_id=uu.user_id 
ORDER BY
usue.time DESC
;

SELECT
ush.name as host,
usfe.time,
uu.user_name as user,
usfet.name as event,
usfe.folder_id,
usfe.folder_name
FROM
up_stats_folder_event usfe,
up_stats_folder_event_type usfet,
up_stats_host ush,
up_user uu
WHERE
usfe.type_id=usfet.id AND 
usfe.host_id=ush.id AND 
usfe.user_id=uu.user_id 
ORDER BY
usfe.time DESC
;

--User sessions
SELECT 
uu.user_id, 
start.type_id, 
start.time
FROM 
up_stats_user_event start, 
up_user uu
WHERE 
uu.user_id=start.user_id AND
(start.type_id=3 OR start.type_id=4)
ORDER BY
start.user_id, 
start.time, 
start.type_id