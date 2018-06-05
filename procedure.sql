set serveroutput on;
drop sequence log_seq;
create sequence log_seq start with 1000;

create or replace trigger delete_enrollments_trigger
before delete on Enrollments
for each row
declare
		curr_time date;
	begin
		select to_char(sysdate, 'DD-MON-YYYY') current_time into curr_time from dual;
		insert into logs values (log_seq.nextval, 'schode1', curr_time,'enrollments','delete',:old.classid);
		update classes set class_size=class_size-1 where classid=:old.classid;
	end;
/

create or replace trigger insert_enrollments_trigger
before insert on Enrollments
for each row
	declare
		curr_time date;
	begin
		select to_char(sysdate, 'DD-MON-YYYY') current_time into curr_time from dual;
		insert into logs values (log_seq.nextval, 'schode1', curr_time,'enrollments','insert',:new.classid);
		update classes set class_size=class_size+1 where classid=:new.classid;
	end;
/


create or replace trigger insert_students_trigger
after insert on Students
for each row
	declare
		curr_time date;
	begin
		select to_char(sysdate, 'DD-MON-YYYY') current_time into curr_time from dual;
		insert into logs values (log_seq.nextval, 'schode1', curr_time,'students','insert',:new.sid);
		--update classes set class_size=class_size-1 where classid=:new.sid;
	end;
/


create or replace trigger delete_students_trigger
after delete on Students
for each row
	declare
		curr_time date;
	begin
		select to_char(sysdate, 'DD-MON-YYYY') current_time into curr_time from dual;
		insert into logs values (log_seq.nextval, 'schode1', curr_time,'students','delete',:old.sid);
		--update classes set class_size=class_size-1 where classid=:old.sid;
	end;
/

create or replace package pack_display as
TYPE stud_ref_cursor IS REF CURSOR;
procedure show_students(rc_s out stud_ref_cursor);
procedure show_courses(rc_c out stud_ref_cursor);
procedure show_classes(rc_c1 out stud_ref_cursor);
procedure show_enrollments(rc_e out stud_ref_cursor);
procedure show_prerequisites(rc_p out stud_ref_cursor);
procedure show_logs(rc_l out stud_ref_cursor);
procedure add_student(s_sid in Students.sid%type,
     s_fname in Students.firstname%type,
     s_lname in Students.lastname%type,
     s_status in Students.status%type,
     s_gpa in Students.gpa%type,
     s_email in Students.email%type);
procedure stud_reg(s_sid in Students.sid%type,rc_studreg in out stud_ref_cursor);
procedure ret_pre_reqs(s_deptcode in prerequisites.dept_code%type,s_courseno in prerequisites.course_no%type,rc_prereq out stud_ref_cursor);
procedure list_class(s_classid in Classes.classid%type,rc_listclass out stud_ref_cursor);
procedure enroll_student(s_sid in Students.sid%type,s_classid in Classes.classid%type,rc_stud_enroll out stud_ref_cursor);
procedure drop_student(s_sid in Students.sid%type,s_classid in Classes.classid%type,rc_stud_drop out stud_ref_cursor);
procedure delete_student(s_sid in Students.sid%type);

end pack_display;
/

create or replace package body pack_display as
procedure show_students(rc_s out stud_ref_cursor)
	is
    begin
		open rc_s for
			select * from students;
    end;
procedure show_courses(rc_c out stud_ref_cursor)
	is
    begin
		open rc_c for
			select * from courses;
    end;
procedure show_classes(rc_c1 out stud_ref_cursor)
	is
    begin
		open rc_c1 for
			select * from classes;
    end;
procedure show_enrollments(rc_e out stud_ref_cursor)
	is
    begin
		open rc_e for
			select * from enrollments;
    end;
procedure show_prerequisites(rc_p out stud_ref_cursor)
	is
    begin
		open rc_p for
			select * from prerequisites;
    end;
procedure show_logs(rc_l out stud_ref_cursor)
	is
    begin
		open rc_l for
			select * from logs;
    end;
procedure add_student
	(s_sid in Students.sid%type,
     s_fname in Students.firstname%type,
     s_lname in Students.lastname%type,
     s_status in Students.status%type,
     s_gpa in Students.gpa%type,
     s_email in Students.email%type) is 
	 begin
	 insert into students values(s_sid,s_fname,s_lname,s_status,s_gpa,s_email);
	 end;
	 
procedure stud_reg
	(s_sid in Students.sid%type,rc_studreg IN out stud_ref_cursor) is
	v_sid Enrollments.sid%type;
	begin
		begin
			open rc_studreg for --if removed it is fetching all sid-= without cursor
			select sid into v_sid from Enrollments where sid=s_sid;
			exception when no_data_found then
				dbms_output.put_line('The student has not taken any course');
				return;
		end;
		begin
		open rc_studreg for
		select s.sid,s.lastname,s.status,c.classid,concat(concat(c1.dept_code,''),c1.course_no) as DEP_CODE,c1.title,c.year,c.semester FROM students s
		INNER JOIN enrollments e on s.sid=e.sid
		INNER JOIN classes c on e.classid=c.classid
		INNER JOIN courses c1 on c.course_no=c1.course_no AND c.dept_code=c1.dept_code
		WHERE s.sid = s_sid;	
		exception
			when no_data_found then
			dbms_output.put_line('Invalid SID');
		end;
end;
procedure ret_pre_reqs
	(s_deptcode in prerequisites.dept_code%type,
     s_courseno in prerequisites.course_no%type,rc_prereq out stud_ref_cursor ) is 
	begin
	open rc_prereq for
	with cte_1(pre_dept_code,pre_course_no,dept_code,course_no) as 
	(select pre_dept_code,pre_course_no,course_no,dept_code
	 from prerequisites
	 where dept_code=s_deptcode and course_no=s_courseno
	union all
	select c.pre_dept_code,c.pre_course_no,c.course_no,c.dept_code from prerequisites c
	join cte_1 p on c.dept_code=p.pre_dept_code and c.course_no=p.pre_course_no
	)
	select pre_dept_code,pre_course_no FROM cte_1;
	 end;
	 
procedure list_class(s_classid in Classes.classid%type,rc_listclass out stud_ref_cursor) is
	v_classid Classes.classid%type;
	v_classsize Classes.class_size%type;
	classsize_exc exception;
	begin
		begin
			open rc_listclass for --if removed it is fetching all classid without cursor
			select classid into v_classid from classes where classid=s_classid;
			exception when no_data_found then
			dbms_output.put_line('Invalid Class id');
			return;
		end;
		begin
		open rc_listclass for
		select class_size into v_classsize from classes where classid=s_classid;
				if(v_classsize<1) then
					raise classsize_exc;
				return;
				end if;
		end;
			begin
			open rc_listclass for
			select s.sid,s.lastname,c2.title,c.classid,c.year,c.semester FROM classes c
			LEFT JOIN enrollments e on e.classid=c.classid
			LEFT JOIN students s on e.sid=s.sid
			JOIN courses c2 on c.course_no=c2.course_no AND c.dept_code=c2.dept_code
			WHERE c.classid = s_classid;
			end;
		exception when classsize_exc then
			dbms_output.put_line('No students in the class');
	end;

procedure enroll_student(
s_sid in Students.sid%type,
s_classid in Classes.classid%type,
rc_stud_enroll out stud_ref_cursor
)is
v_sid Students.sid%type;
v_classid Classes.classid%type;
v_classsize Classes.class_size%type;
v_deptcode Classes.dept_code%type;
v_courseno Classes.course_no%type;
v_count number;
v_count1 number;
v_count2 number;
v_limit Classes.limit%type;
limit_exp exception;
begin
					begin
					select sid into v_sid from students where sid=s_sid;
					exception when no_data_found then
								dbms_output.put_line('Invalid SID');
					return;
					end;
					
					begin
						BEGIN
						select classid,class_size,limit into v_classid,v_classsize,v_limit from classes where classid=s_classid;
						exception when no_data_found then
									dbms_output.put_line('Invalid Class ID');return;
						END;
						if(v_classsize=v_limit) then
							dbms_output.put_line('The class is closed'); return;
						end if;	
					end;
					
			begin
			select count(*) into v_count from enrollments e join classes c on c.classid = e.classid where e.sid = s_sid and c.semester in (select semester from classes where classid =s_classid) and c.year in (select year from classes where classid = s_classid);
			if(v_count=1) then
			dbms_output.put_line('Currently enrolled in 1 class');
			elsif(v_count=2) then
			dbms_output.put_line('You are overloaded');
			elsif(v_count>=3) then
			dbms_output.put_line('Students cannot be enrolled in more than three classes in the same semester.');
			return;
			else
			NULL;
			end if;
		end;
		
		BEGIN
		select dept_code,course_no into v_deptcode,v_courseno from classes where classid=s_classid;
		select count(*) into v_count2 from enrollments where sid=s_sid;
		BEGIN
		select count(*) into v_count1 from enrollments e where e.sid =s_sid
			and not exists
						(select c.classid from classes c where (c.dept_code,c.course_no) in 
						(select *from (with cte_1(pre_dept_code,pre_course_no,dept_code,course_no) as 
						(select pre_dept_code,pre_course_no,course_no,dept_code
							 from prerequisites
						 where dept_code=v_deptcode and course_no=v_courseno
							union all
						select c.pre_dept_code,c.pre_course_no,c.course_no,c.dept_code from prerequisites c
						join cte_1 p on c.dept_code=p.pre_dept_code and c.course_no=p.pre_course_no
						)
				select pre_dept_code,pre_course_no FROM cte_1)B) and c.classid=e.classid and e.lgrade in ('A','B','C','D') and e.classid!=s_classid);
			if(v_count1 = 0 AND v_count2!=0) then dbms_output.put_line('Prerequisite courses have not been completed.');
			open rc_stud_enroll for select distinct 0 as count from enrollments;
			return;
			END IF;
		END;
					
		END;
		
		BEGIN
		open rc_stud_enroll for select distinct 1 as count from enrollments;		
		insert into enrollments values (s_sid,s_classid,null);
		dbms_output.put_line('Successfully enrolled');
		END;
end;

procedure drop_student(
s_sid in Students.sid%type,
s_classid in Classes.classid%type,
rc_stud_drop out stud_ref_cursor
)is
v_sid Students.sid%type;
v_classid Classes.classid%type;
v_classsize Classes.class_size%type;
v_deptcode Classes.dept_code%type;
v_courseno Classes.course_no%type;
vcount number;
v_count1 number;
v_count2 number;
v_limit Classes.classid%type;
begin
					begin
					select sid into v_sid from students where sid=s_sid;
					exception when no_data_found then
								dbms_output.put_line('Invalid SID');
					return;
					end;
					
					begin
						BEGIN
						select classid,class_size,limit into v_classid,v_classsize,v_limit from classes where classid=s_classid;
						exception when no_data_found then
									dbms_output.put_line('Invalid Class ID');return;
						END;
						BEGIN
						select sid into v_sid from enrollments where classid=s_classid and sid=s_sid;
						exception when no_data_found then
									dbms_output.put_line('The student is not enrolled in the class.');return;
						END;
					end;

		
	begin
		select c.dept_code,c.course_no into v_deptcode,v_courseno from classes c where c.classid=s_classid;
		select count(*) into vcount from enrollments e
		join classes c on e.classid=c.classid and e.classid!=s_classid
		where sid=s_sid and EXISTS
		(select distinct * from (select cs.dept_code,cs.course_no from (
													WITH Child (pre_dept_code,pre_course_no, dept_code,course_no)
																			AS
																			(SELECT pre_dept_code,pre_course_no, dept_code,course_no
																			FROM prerequisites m
																			WHERE pre_dept_code =v_deptcode and pre_course_no =v_courseno
																			UNION ALL	
																			SELECT  m.pre_dept_code,m.pre_course_no, m.dept_code,m.course_no
																			FROM prerequisites m
																			INNER JOIN Child p
																				ON p.dept_code = m.pre_dept_code and p.course_no = m.pre_course_no
																			)
														SELECT dept_code,course_no
														FROM Child)B
					JOIN
					classes cs on cs.dept_code=B.dept_code and cs.course_no =B.course_no) D) GROUP BY v_deptcode,v_courseno;
			exception when no_data_found then
			open rc_stud_drop for select distinct 0 as count from enrollments;
			select count(*) into v_count1 from enrollments where classid=s_classid;
			select count(*) into v_count2 from enrollments where sid=s_sid;
			if(v_count2=1) then
				dbms_output.put_line('This student is not enrolled in any classes');
			end if;
			if(v_count1=1) then
				dbms_output.put_line('The class now has no students.');
			end if;
			dbms_output.put_line(s_sid || ' has been successfully dropped');
			delete from enrollments where sid=s_sid and classid=s_classid;
			return;
	end;
		begin
		open rc_stud_drop for
			select distinct 1 as count from enrollments;
		dbms_output.put_line(vcount);
		if(vcount!=0) then
		dbms_output.put_line('The drop is not permitted because another class uses it as a prerequisite.');
		return;
		end if;
		end;

end;

procedure delete_student(s_sid in Students.sid%type) is
	v_sid Students.sid%type;
	begin
		begin
		select sid into v_sid from students where sid=s_sid;
		exception when no_data_found then
			dbms_output.put_line('Invalid SID');
		end;
	delete from enrollments where sid=s_sid;
	delete from students where sid=s_sid;
	end;

end pack_display;
/