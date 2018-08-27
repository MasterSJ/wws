package cn.wws.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <B>系统名称：RPC调用</B><BR>
 * <B>模块名称：http工具类</B><BR>
 * <B>中文类名：</B><BR>
 * <B>概要说明：</B><BR>
 * 
 * @author（sunxuekun） @since 2017年6月29日
 */
public class HttpExecutor {
    
    private final static Logger LOGGER            = LoggerFactory.getLogger(HttpExecutor.class);

    public static final Integer MAX_IDLE_TIME_OUT = Integer.valueOf(60000);

    public static HttpClient    httpClient        = null;

    public String post(String url, String content) throws IOException {
        return post(url, content, "application/json", "utf-8");
    }

    public String post(String url, String content, String contentType, String charset)
            throws IOException {
        PostMethod post = new PostMethod(url);
        post.setRequestEntity(new StringRequestEntity(content, contentType, charset));
        try {
            httpClient.executeMethod(post);
            String str = streamToString(post.getResponseBodyAsStream(), Charset.forName(charset));
            return str;
        } finally {
            post.releaseConnection();
        }
    }

    public String get(String url, String content) throws IOException {
        return get(url, content, "text/json", "utf-8");
    }

    public String get(String url, String content, String contentType, String charset)
            throws IOException {
        GetMethod get = new GetMethod(url);
        try {
            httpClient.executeMethod(get);
            String str = streamToString(get.getResponseBodyAsStream(), Charset.forName(charset));
            return str;
        } finally {
            get.releaseConnection();
        }
    }
    
    public String getWithHeader(String url, String content,Map<String,String> headers) throws IOException {
        return getWithHeader(url, content, "text/json", "utf-8",headers);
    }
    
    public String getWithHeader(String url, String content, String contentType, String charset,Map<String,String> headers)
            throws IOException {
        GetMethod get = new GetMethod(url);
        try {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                get.addRequestHeader(entry.getKey(), entry.getValue());
            }
            int ret = httpClient.executeMethod(get);
            if(ret != 200)
            {
                return "";
            }
            String str = streamToString(get.getResponseBodyAsStream(), Charset.forName(charset));
            return str;
        } finally {
            get.releaseConnection();
        }
    }

    public String put(String url, String content) throws IOException {
        return put(url, content, "application/json", "utf-8");
    }

    public String put(String url, String content, String contentType, String charset)
            throws IOException {
        PutMethod put = new PutMethod(url);
        put.setRequestEntity(new StringRequestEntity(content, contentType, charset));
        try {
            httpClient.executeMethod(put);
            String str = streamToString(put.getResponseBodyAsStream(), Charset.forName(charset));
            return str;
        } finally {
            put.releaseConnection();
        }
    }

    public String streamToString(InputStream stream, Charset charset) throws IOException {
        String respLine;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
        StringBuilder respBuilder = new StringBuilder();

        while ((respLine = reader.readLine()) != null) {
            respBuilder.append(respLine);
        }
        return respBuilder.toString();
    }

    static {
        MultiThreadedHttpConnectionManager connectionManager =
                new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(20);
        connectionManager.getParams().setMaxTotalConnections(50);
        connectionManager.closeIdleConnections(MAX_IDLE_TIME_OUT.intValue());
        httpClient = new HttpClient(connectionManager);
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
    }
}
