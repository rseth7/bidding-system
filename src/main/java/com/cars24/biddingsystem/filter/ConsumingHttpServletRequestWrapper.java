package com.cars24.biddingsystem.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

@Slf4j
final public class ConsumingHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private static final int CONSUMING_CHUNK_SIZE = 1024;
    private InputStream cachedInputStream;
    private TeeServletInputStream servletInputStream;

    public ConsumingHttpServletRequestWrapper(ContentCachingRequestWrapper request) {
        super(request);
        request.getParameterMap();
        consume();
    }

    @Override
    public ContentCachingRequestWrapper getRequest() {
        return (ContentCachingRequestWrapper) super.getRequest();
    }

    @Override
    public ServletInputStream getInputStream() {
        if(servletInputStream != null) {
            servletInputStream = new TeeServletInputStream();
        }
        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
    }

    @Override
    public String getCharacterEncoding() {
        final String encoding = super.getCharacterEncoding();
        return (encoding != null ? encoding : WebUtils.DEFAULT_CHARACTER_ENCODING);
    }

    public byte[] getContentAsByteArray() {
        return getRequest().getContentAsByteArray();
    }

    private void consume() {
        ServletInputStream originalInputStream = null;
        try {
            originalInputStream = getRequest().getInputStream();
            consume(originalInputStream);
            this.cachedInputStream = new ByteArrayInputStream(getRequest().getContentAsByteArray());
        } catch(final IOException e) {
            log.error("Error while consuming the request with message : {}", e.getMessage(), e);
        } finally {
            closeStream(originalInputStream);
        }
    }

    private void consume(InputStream stream) throws IOException {
        byte[] chunk = new byte[CONSUMING_CHUNK_SIZE];
        int consumedBytes;
        do{
           consumedBytes = stream.read(chunk, 0, CONSUMING_CHUNK_SIZE);
        } while(consumedBytes != -1);
    }

    private void closeStream(final ServletInputStream stream) {
        if(stream != null) {
            try {
              stream.close();
            } catch (final IOException e) {
                log.error("Error while closing the request with message : {}",
                        e.getMessage(), e);
            }
        }
    }

    private class TeeServletInputStream extends ServletInputStream {
        @Override
        public boolean isFinished() {
            throw new RuntimeException();
        }

        @Override
        public boolean isReady() {
            throw new RuntimeException();
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new RuntimeException();
        }

        @Override
        public int read() throws IOException {
            return cachedInputStream.read();
        }
    }
}
