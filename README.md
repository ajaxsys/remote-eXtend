A simple Servlet for local server file browsing & download.

USAGE:
---------------

1.Put this jar(no dependency) to your classpath.

2.Edit your /WEB-INF/web.xml, add servlet as bellow:

		<servlet>
				<servlet-name>file-download</servlet-name>
				<servlet-class>exec.download.FileDownloadServlet</servlet-class>
		</servlet>

		<servlet-mapping>
				<servlet-name>file-download</servlet-name>
				<url-pattern>/fd</url-pattern>
		</servlet-mapping>

3.Then run your Java Servlet Server, e.g: `mvn jetty:run`.
  Now, you can run it on url: `http://localhost:8080/fd`