CREATE TABLE company
(
    id integer NOT NULL,
    c_name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

insert into company(id, c_name) 
values
(1, 'Ikea'),
(2, 'Nokia'),
(3, 'PepsiCo'),
(4, 'General Motors'),
(5, 'FedEx');

CREATE TABLE person
(
    id integer NOT NULL,
   	p_name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

insert into person(id, p_name, company_id) 
values
(1, 'A.Flowers', 1),
(2, 'D.Hensley', 1),
(3, 'P.Green', 2),
(4, 'A.Mcclain', 2),
(5, 'M.Dillon', 2),
(6, 'J.Carter', 3),
(7, 'P.Roach', 3),
(8, 'O.Fritz', 3),
(9, 'K.Crawford', 3),
(10, 'F.Huber', 3),
(11, 'E.Dickson', 4),
(12, 'L.Carson', 4),
(13, 'B.Zimmerman', 5),
(14, 'T.Keller', 5),
(15, 'G.Callahan', 5),
(16, 'N.Armstrong', 5),
(17, 'S.Franco', 5);

--first task
select p.p_name,c.c_name 
from person p join company c on c.id = p.company_id 
where p.company_id != 5;

--second task
create view max_emp
as select c.c_name, count(p.id) as emp_count
from company c join person p on c.id = p.company_id
group by c.c_name;

select c_name, emp_count from max_emp 
where emp_count = (select max(emp_count) from max_emp);