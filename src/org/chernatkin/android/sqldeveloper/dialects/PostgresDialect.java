package org.chernatkin.android.sqldeveloper.dialects;

import java.io.File;

import org.chernatkin.android.sqldeveloper.R;

public class PostgresDialect extends SQLDialect {

	public PostgresDialect() {
		super(R.string.title_postgres, getApplicationRoot() + File.separator  + "postgresql" + File.separator + "postgresqldb");
		getProps().setProperty("sql.syntax_pgs", "true");
	}

	@Override
	public BatchBuilder getDemoSchemaScript() {
		
		return new BatchBuilderImpl("CREATE TABLE DEPT " +
			       "(DEPTNO NUMERIC(2) PRIMARY KEY, " +
			       "DNAME VARCHAR(14), " +
			       "LOC VARCHAR(13));")
		
		.addQuery("INSERT INTO DEPT VALUES (10, 'ACCOUNTING', 'NEW YORK');")
		.addQuery("INSERT INTO DEPT VALUES (20, 'RESEARCH',   'DALLAS');")
		.addQuery("INSERT INTO DEPT VALUES (30, 'SALES',      'CHICAGO');")
		.addQuery("INSERT INTO DEPT VALUES (40, 'OPERATIONS', 'BOSTON');")
		
		.addQuery("CREATE TABLE EMP " +
			       "(EMPNO NUMERIC(4) NOT NULL, " +
			       "ENAME VARCHAR(10), " +
			       "JOB VARCHAR(9), " +
			       "MGR NUMERIC(4), " +
			       "HIREDATE DATE, " +
			       "SAL NUMERIC(7, 2) NOT NULL DEFAULT 0, " +
			       "COMM NUMERIC(7, 2), " +
			       "DEPTNO NUMERIC(2)," +
			       "FOREIGN KEY (DEPTNO) REFERENCES DEPT(DEPTNO) ON DELETE CASCADE);")
			       
		.addQuery("INSERT INTO EMP VALUES (7369, 'SMITH',  'CLERK',     7902, TO_DATE('17-DEC-1980', 'DD-MON-YYYY'),  800, NULL, 20);")
		.addQuery("INSERT INTO EMP VALUES (7369, 'SMITH',  'CLERK',     7902, TO_DATE('17-DEC-1980', 'DD-MON-YYYY'),  800, NULL, 20);")
		.addQuery("INSERT INTO EMP VALUES (7499, 'ALLEN',  'SALESMAN',  7698, TO_DATE('20-FEB-1981', 'DD-MON-YYYY'), 1600,  300, 30);")
		.addQuery("INSERT INTO EMP VALUES (7521, 'WARD',   'SALESMAN',  7698, TO_DATE('22-FEB-1981', 'DD-MON-YYYY'), 1250,  500, 30);")
		.addQuery("INSERT INTO EMP VALUES (7566, 'JONES',  'MANAGER',   7839, TO_DATE('2-APR-1981', 'DD-MON-YYYY'),  2975, NULL, 20);")
		.addQuery("INSERT INTO EMP VALUES (7654, 'MARTIN', 'SALESMAN',  7698, TO_DATE('28-SEP-1981', 'DD-MON-YYYY'), 1250, 1400, 30);")
		.addQuery("INSERT INTO EMP VALUES (7698, 'BLAKE',  'MANAGER',   7839, TO_DATE('1-MAY-1981', 'DD-MON-YYYY'),  2850, NULL, 30);")
		.addQuery("INSERT INTO EMP VALUES (7782, 'CLARK',  'MANAGER',   7839, TO_DATE('9-JUN-1981', 'DD-MON-YYYY'),  2450, NULL, 10);")
		.addQuery("INSERT INTO EMP VALUES (7788, 'SCOTT',  'ANALYST',   7566, TO_DATE('09-DEC-1982', 'DD-MON-YYYY'), 3000, NULL, 20);")
		.addQuery("INSERT INTO EMP VALUES (7839, 'KING',   'PRESIDENT', NULL, TO_DATE('17-NOV-1981', 'DD-MON-YYYY'), 5000, NULL, 10);")
		.addQuery("INSERT INTO EMP VALUES (7844, 'TURNER', 'SALESMAN',  7698, TO_DATE('8-SEP-1981', 'DD-MON-YYYY'),  1500,    0, 30);")
		.addQuery("INSERT INTO EMP VALUES (7876, 'ADAMS',  'CLERK',     7788, TO_DATE('12-JAN-1983', 'DD-MON-YYYY'), 1100, NULL, 20);")
		.addQuery("INSERT INTO EMP VALUES (7900, 'JAMES',  'CLERK',     7698, TO_DATE('3-DEC-1981', 'DD-MON-YYYY'),   950, NULL, 30);")
		.addQuery("INSERT INTO EMP VALUES (7902, 'FORD',   'ANALYST',   7566, TO_DATE('3-DEC-1981', 'DD-MON-YYYY'),  3000, NULL, 20);")
		.addQuery("INSERT INTO EMP VALUES (7934, 'MILLER', 'CLERK',     7782, TO_DATE('23-JAN-1982', 'DD-MON-YYYY'), 1300, NULL, 10);")
		


	
		.addQuery("CREATE TABLE BONUS " +
		        "(ENAME VARCHAR(10), " + 
		        "JOB   VARCHAR(9), " +
		        "SAL   NUMERIC, " +
		        "COMM  NUMERIC);")
	
		.addQuery("CREATE TABLE SALGRADE" +
			        "(GRADE NUMERIC, " +
			        "LOSAL NUMERIC, " +
			        "HISAL NUMERIC);")
	
		.addQuery("INSERT INTO SALGRADE VALUES (1,  700, 1200);")
		.addQuery("INSERT INTO SALGRADE VALUES (2, 1201, 1400);")
		.addQuery("INSERT INTO SALGRADE VALUES (3, 1401, 2000);")
		.addQuery("INSERT INTO SALGRADE VALUES (4, 2001, 3000);")
		.addQuery("INSERT INTO SALGRADE VALUES (5, 3001, 9999);");
	}
}
