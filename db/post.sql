create table post(
	id serial primary key,
	title text,
	text text,
	link text unique,
	created timestamp
);