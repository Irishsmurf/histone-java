package ru.histone.acceptance.websever;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class TestHandler extends AbstractHandler {

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		StringBuilder b = new StringBuilder();
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		//
		final String path = request.getPathInfo();
		final String query = request.getQueryString();
		final String method = request.getMethod();
		//
		b = new StringBuilder();
		for (Enumeration<String> it = request.getHeaderNames(); it.hasMoreElements();) {
			String name = it.nextElement();
			b.append(", \"").append(name).append("\": \"").append(request.getHeader(name)).append("\"");
		}
		final String headers = b.length() == 0 ? "{ }" : "{" + b.substring(1) + "}";
		//
		b = new StringBuilder();
		final BufferedReader bodyReader = request.getReader();
		b.append("");
		char[] charBuffer = new char[128];
		int bytesRead = -1;
		while ((bytesRead = bodyReader.read(charBuffer)) > 0) {
			b.append(charBuffer, 0, bytesRead);
		}
		final String body = b.toString();
		//
		b = new StringBuilder();
		b.append("{\n");
		b.append("path: \"").append(path).append("\",\n");
		b.append("query: \"").append(query).append("\",\n");
		b.append("method: \"").append(method).append("\",\n");
		b.append("headers: ").append(headers).append(",\n");
		b.append("body: \"").append(body).append("\"\n");
		b.append("}");
		response.getWriter().println(b.toString());
	}
}