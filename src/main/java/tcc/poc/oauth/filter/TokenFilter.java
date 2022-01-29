package tcc.poc.oauth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.stereotype.Component;
import tcc.poc.constants.AppConstants;
import tcc.poc.model.BearerToken;
import tcc.poc.model.TokenRetorno;
import tcc.poc.model.User;
import tcc.poc.oauth.DynamoClient;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@WebFilter
@Component
public class TokenFilter implements Filter {

    @Inject
    private DynamoClient service;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        try {
            User user = getUserByUsernamePassword(request);
            HtmlResponseWrapper newResponse = new HtmlResponseWrapper((HttpServletResponse) response);
            chain.doFilter(request, newResponse);
            String servletResponseStr = newResponse.getCaptureAsString();
            String clientId = getParameter(request, "client_id");
            String grantType = getParameter(request, "grant_type");

            if(((HttpServletRequest)request).getServletPath().contains("oauth")) {

                ObjectMapper om = new ObjectMapper();
                BearerToken bearerToken = om.readValue(servletResponseStr, BearerToken.class);
                bearerToken.setExpires_in(3600);

                if(AppConstants.GRANT_TYPE_PASSWORD.equalsIgnoreCase(grantType)) {

                    String username = getParameter(request, "username");

                    newResponse.setStatus(200);
                    TokenRetorno tokenRetorno = new TokenRetorno(bearerToken.getExpires_in(),
                            getScope(bearerToken.getScope()), clientId, username, user.getTypeClient());
                    response.getOutputStream().write(om.writeValueAsBytes(tokenRetorno));

                } else {

                    TokenRetorno tokenRetorno = new TokenRetorno(bearerToken.getExpires_in(), getScope(bearerToken.getScope()), clientId);
                    response.getOutputStream().write(om.writeValueAsBytes(tokenRetorno));

                }

            } else {
                response.getOutputStream().write(servletResponseStr.getBytes());
            }
        }catch (SecurityException e) {
            ((HttpServletResponse) response).sendError(401, e.getMessage());
        }catch (Exception e) {
            ((HttpServletResponse) response).sendError(500, e.getMessage());
        }
    }

    private User getUserByUsernamePassword(ServletRequest request) {

        String grantType = getParameter(request, "grant_type");
        if(AppConstants.GRANT_TYPE_PASSWORD.equalsIgnoreCase(grantType)) {

            String username = getParameter(request, "username");
            String password = getParameter(request, "password");
            User user = this.service.loadUserByUsernameAndPassword(username, password);

            if(user == null) {
                throw new SecurityException("Invalid username/password");
            }

            return user;

        }

        return null;
    }

    @Override
    public void destroy() {

    }

    private class HtmlResponseWrapper extends HttpServletResponseWrapper {

        private final ByteArrayOutputStream capture;
        private ServletOutputStream output;
        private PrintWriter writer;
        private int status;

        public HtmlResponseWrapper(HttpServletResponse response) {
            super(response);
            capture = new ByteArrayOutputStream(response.getBufferSize());
        }

        @Override
        public void setStatus(int sc) {
            super.setStatus(200);
            this.status = sc;
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if(writer != null) {
                throw new IllegalStateException("erro");
            }

            if(output == null) {

                output = new ServletOutputStream() {
                    @Override
                    public boolean isReady() {
                        return false;
                    }

                    @Override
                    public void setWriteListener(WriteListener listener) {

                    }

                    @Override
                    public void write(int b) throws IOException {
                        capture.write(b);
                    }

                    @Override
                    public void close() throws IOException {
                        capture.close();
                    }

                    @Override
                    public void flush() throws IOException {
                        capture.flush();
                    }
                };

            }

            return output;
        }


        @Override
        public PrintWriter getWriter() throws UnsupportedEncodingException {
            if(output != null ) {
                throw new IllegalStateException("erro");
            }

            if(writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(capture, getCharacterEncoding()));
            }

            return writer;
        }

        @Override
        public void flushBuffer() throws IOException {
            super.flushBuffer();
            if(writer != null) {
                writer.flush();
            } else {
                output.flush();
            }
        }

        byte[] getCaptureAsBytes() throws IOException {
            if(writer != null) {
                writer.close();
            } else if(output != null) {
                output.close();
            }
            return capture.toByteArray();
        }

        String getCaptureAsString() throws IOException {
            return new String(getCaptureAsBytes(), getCharacterEncoding());
        }
    }

    private static Set<String> getScope(String scope) {
        if(scope != null) {
            return new HashSet<>(Arrays.asList(scope.split(" ")));
        }
        return null;
    }

    private String getParameter(ServletRequest request, String nameParameter) {
        String[] p = request.getParameterMap().getOrDefault(nameParameter, new String[]{""});
        return p[0];
    }


}
