package org.zb.plugin.putil;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : ZhouBin
 * @date :  2024/1/9
 */
@Slf4j
public class HttpUtil {

    public static final String HTTPS = "https";
    public static final String CHARSET = "UTF-8";
    public static final int DEFAULT_CONNECTION_TIMEOUT = 5000;
    public static final int DEFAULT_SO_TIMEOUT = 30000;

    private static final RequestConfig config = RequestConfig.custom()
            .setSocketTimeout(DEFAULT_SO_TIMEOUT)
            .setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
            .setConnectionRequestTimeout(DEFAULT_CONNECTION_TIMEOUT)
            .build();


    /**
     * 发送 Get http请求
     */
    public static String get(String reqUrl) {

        return sendGet(reqUrl, null, CHARSET, CHARSET, null, null);
    }

    /**
     * 发送 Get http请求
     */
    public static String get(String reqUrl, String spliceParam) {

        return sendGet(reqUrl, spliceParam, CHARSET, CHARSET, null, null);
    }


    /**
     * 发送 Get http请求
     */
    public static String get(String reqUrl, Map<String, String> params) {
        return sendGet(reqUrl, params, CHARSET, CHARSET, null, null);
    }


    /**
     * 发送 https 或者http 请求 ，兼容版
     */
    public static String post(String reqUrl, Object params) {
        return post(reqUrl, params, CHARSET);
    }

    /**
     * 发送 https 或者http 请求 ，兼容版
     */
    public static String post(String reqUrl, Object params, Map<String, String> headerMap) {
        return sendPost(reqUrl, params, CHARSET, CHARSET, null, headerMap);
    }

    /**
     * 发送 https 或者http 请求 ，兼容版
     */
    public static String post(String reqUrl, Object params, String charset) {
        return sendPost(reqUrl, params, charset, charset, null, null);
    }

    private static CloseableHttpClient getCloseableHttpClient(SSLContext sslcontext) throws Exception {
        if (sslcontext == null) {
            sslcontext = SSLContext.getInstance("SSL");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslcontext.init(null, new TrustManager[]{tm}, null);
        }
//         SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext, new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2", "TLSv1.1"},
                null, (s, sslSession) -> true);
        return HttpClientBuilder.create().setSSLSocketFactory(sslsf).build();
    }

    private static String getStreamString(InputStream inputStream, String encode) throws Exception {
        BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(inputStream, encode));
        StringBuilder tStringBuffer = new StringBuilder();
        String sTempOneLine;
        while ((sTempOneLine = tBufferedReader.readLine()) != null) {
            tStringBuffer.append(sTempOneLine);
        }
        return tStringBuffer.toString();
    }


    /**
     * 发生https请求
     *
     * @param reqUrl         请求地址
     * @param params         参数
     * @param sendCharset    发送报文编码
     * @param receiveCharset 接受报文解码 编码
     * @param sslContext     SSL
     * @param headerMap      指定header中参数
     * @return 返回值
     */
    public static String sendPost(String reqUrl, Object params, String sendCharset, String receiveCharset, SSLContext sslContext, Map<String, String> headerMap) {
        CloseableHttpClient httpClient = null;
        try {
            if (reqUrl.startsWith(HTTPS)) {
                httpClient = getCloseableHttpClient(sslContext);
            } else {
                httpClient = HttpClients.createDefault();
            }
            HttpPost httpPost = new HttpPost(reqUrl);
            httpPost.setConfig(config);
            if (null != params) {
                HttpEntity entity = null;
                if (params instanceof String) {
                    if (StringUtils.isNotEmpty((String) params)) {
                        entity = new StringEntity((String) params, sendCharset);
                    }
                    httpPost.setHeader("Content-Type", "application/json; charset=" + sendCharset);
                } else if (params instanceof Map) {
                    Map<String, Object> paramsMap = (Map<String, Object>) params;
                    List<NameValuePair> nvps = new ArrayList<>(paramsMap.size());
                    Set<Entry<String, Object>> set = paramsMap.entrySet();
                    for (Entry<String, Object> entry : set) {
                        if (entry.getValue() == null) {
                            continue;
                        }
                        nvps.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
                    }
                    entity = new UrlEncodedFormEntity(nvps, sendCharset);
                    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                } else {
                    entity = new StringEntity(JSON.toJSONString(params), sendCharset);
                    httpPost.setHeader("Content-Type", "application/json; charset=" + sendCharset);
                }
                httpPost.setEntity(entity);
            }
            httpPost.setHeader("Accept", "application/json");

            if (headerMap != null) {
                headerMap.keySet().forEach(key -> httpPost.setHeader(key, headerMap.get(key)));
            }
            HttpResponse response = httpClient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String value;
                if (receiveCharset != null) {
                    value = getStreamString(entity.getContent(), receiveCharset);
                } else {
                    value = EntityUtils.toString(entity, sendCharset);
                }
                EntityUtils.consume(entity);
                return value;
            } else {
                throw new RuntimeException("Http响应码异常：" + statusLine.getStatusCode() + " - " + statusLine.getReasonPhrase());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(httpClient);
        }
    }


    public static String sendGet(String reqUrl, Object params, String sendCharset, String receiveCharset, SSLContext sslContext, Map<String, String> headerMap) {
        CloseableHttpClient httpClient = null;
        try {
            if (reqUrl.startsWith(HTTPS)) {
                httpClient = getCloseableHttpClient(sslContext);
            } else {
                httpClient = HttpClients.createDefault();
            }
            String url = reqUrl;
            if (null != params) {
                if (params instanceof String) {
                    url = url + "?" + params;
                } else if (params instanceof Map) {
                    Map<String, Object> paramsMap = (Map<String, Object>) params;
                    url = url + "?" + paramsMap.keySet().stream().map(o -> o + "=" + paramsMap.get(o)).collect(Collectors.joining("&", "", ""));
                }
            }
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(config);

            httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
            if (headerMap != null) {
                headerMap.keySet().forEach(key -> httpGet.setHeader(key, headerMap.get(key)));
            }

            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String value;
                if (receiveCharset != null) {
                    value = getStreamString(entity.getContent(), receiveCharset);
                } else {
                    value = EntityUtils.toString(entity, sendCharset);
                }
                EntityUtils.consume(entity);
                return value;
            } else {
                throw new RuntimeException("Http响应码异常：" + statusLine.getStatusCode() + " - " + statusLine.getReasonPhrase());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(httpClient);
        }
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
