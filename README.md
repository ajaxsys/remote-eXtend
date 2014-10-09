A simple Servlet for local server file browsing & download.

USAGE:
---------------

1.Put this jar and H2-1.x.y.jar to your classpath.

2.Edit your /WEB-INF/web.xml, add servlet as bellow:

		<servlet>
				<servlet-name>H2Console</servlet-name>
				<servlet-class>org.h2.server.web.WebServlet</servlet-class>
				<init-param>
						<param-name>webAllowOthers</param-name>
						<param-value>true</param-value>
				</init-param>
		</servlet>
		<servlet>
				<servlet-name>FileDownloader</servlet-name>
				<servlet-class>exec.download.FileDownloadServlet</servlet-class>
		</servlet>
		<servlet>
				<servlet-name>APP_Dispacher</servlet-name>
				<servlet-class>exec.download.DispacherServlet</servlet-class>
		</servlet>

		<servlet-mapping>
				<servlet-name>APP_Dispacher</servlet-name>
				<url-pattern>/dspch/*</url-pattern>
		</servlet-mapping>

3.Then run your Java Servlet Server (For maven user: `mvn jetty:run`).
  Now, you can run it on url: `http://localhost:8080/dspch`