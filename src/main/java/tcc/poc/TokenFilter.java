package tcc.poc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

@WebFilter
@Component
public class TokenFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HtmlResponseWrapper newResponse = new HtmlResponseWrapper((HttpServletResponse) response);
            chain.doFilter(request, newResponse);
            String servletResponseStr = newResponse.getCaptureAsString();

            if(((HttpServletRequest)request).getServletPath().contains("oauth")) {

                ObjectMapper om = new ObjectMapper();
                NewToken nt = om.readValue(servletResponseStr, NewToken.class);

                response.getOutputStream().write(om.writeValueAsBytes(nt));

            } else {
              response.getOutputStream().write(servletResponseStr.getBytes());
            }
        }catch (Exception e) {
            ((HttpServletResponse) response).sendError(500, e.getMessage());
        }
    }

    @Override
    public void destroy() {

    }

    private class HtmlResponseWrapper extends HttpServletResponseWrapper {

        private final ByteArrayOutputStream capture;
        private ServletOutputStream output;
        private PrintWriter writer;

        public HtmlResponseWrapper(HttpServletResponse response) {
            super(response);
            capture = new ByteArrayOutputStream(response.getBufferSize());
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class NewToken {
        @JsonProperty
        private String access_token;
        @JsonProperty
        private String scope;
        @JsonProperty
        private String token_type;
        @JsonProperty
        private int expires_in;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getToken_type() {
            return token_type;
        }

        public void setToken_type(String token_type) {
            this.token_type = token_type;
        }

        public int getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(int expires_in) {
            this.expires_in = expires_in;
        }
    }
}
