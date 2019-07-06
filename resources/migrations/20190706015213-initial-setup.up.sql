create table if not exists api_key (
       id serial not null primary key,
       name text not null,
       unique(name)
);
--;;

create table if not exists response (
       id serial not null primary key,
       api_id integer not null, 
       endpoint text not null,
       query json, 
       response json,
       match_query boolean default(false),

       foreign key(api_id) references api_key (id),
       unique(api_id, endpoint) 
);
--;;

-- seed data
insert into api_key (name) values
('api1'),
('api2'),
('api3')
;

--;;

insert into response
(api_id, endpoint, query, response, match_query) 
values
(1, 'testing', null, '{"api": "api1", "hello": "world"}', default),
(1, 'testing/query', '{"query-value": true}', '{"api": "api1", "hello": "world"}', true),
(1, 'empty/response', null, null, default),
(2, 'testing1', null, '{"api": "api2", "some": "data"}', default),
(2, 'testing1/nested', null, '{"api": "api2", "data": {"nested": "data"}}', default)
;
