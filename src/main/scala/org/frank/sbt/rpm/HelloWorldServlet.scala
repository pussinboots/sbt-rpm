package org.frank.sbt.rpm;

import javax.servlet.http._

class HelloWorldServlet extends HttpServlet {
	override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
		resp.getWriter().print("Hello World from updated sbt rpm!")
	}
}
