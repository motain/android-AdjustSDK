package com.adjust.sdk;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import android.content.Context;

import java.security.KeyStore;

public class AdjustFactory {
    private static IPackageHandler packageHandler = null;
    private static IRequestHandler requestHandler = null;
    private static Logger logger = null;
    private static HttpClient httpClient = null;
    private static SchemeRegistry registry = null;

    private static long timerInterval = -1;
    private static long sessionInterval = -1;
    private static long subsessionInterval = -1;

    public static IPackageHandler getPackageHandler(ActivityHandler activityHandler, Context context, boolean dropOfflineActivities) {
        if (packageHandler == null) {
            return new PackageHandler(activityHandler, context, dropOfflineActivities);
        }
        return packageHandler;
    }

    public static IRequestHandler getRequestHandler(IPackageHandler packageHandler) {
        if (requestHandler == null) {
            return new RequestHandler(packageHandler);
        }
        return requestHandler;
    }

    public static Logger getLogger() {
        if (logger == null) {
            // Logger needs to be "static" to retain the configuration throughout the app
            logger = new LogCatLogger();
        }
        return logger;
    }

    public static HttpClient getHttpClient(HttpParams params) {
        if (httpClient == null) {
            try
            {
                if (registry == null) {
                    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    trustStore.load(null, null);

                    SSLSocketFactory sf = new AdjustSSLSocketFactory(trustStore);
                    sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                    registry = new SchemeRegistry();
                    registry.register(new Scheme("https", sf, 443));
                }
                ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

                return new DefaultHttpClient(ccm, params);
            }
            catch (Exception e)
            {
                return new DefaultHttpClient(params);
            }
        }
        return httpClient;
    }
    public static long getTimerInterval() {
        if (timerInterval == -1) {
            return Constants.ONE_MINUTE;
        }
        return timerInterval;
    }

    public static long getSessionInterval() {
        if (sessionInterval == -1) {
            return Constants.THIRTY_MINUTES;
        }
        return sessionInterval;
    }

    public static long getSubsessionInterval() {
        if (subsessionInterval == -1) {
            return Constants.ONE_SECOND;
        }
        return subsessionInterval;
    }

    public static void setPackageHandler(IPackageHandler packageHandler) {
        AdjustFactory.packageHandler = packageHandler;
    }

    public static void setRequestHandler(IRequestHandler requestHandler) {
        AdjustFactory.requestHandler = requestHandler;
    }

    public static void setLogger(Logger logger) {
        AdjustFactory.logger = logger;
    }

    public static void setHttpClient(HttpClient httpClient) {
        AdjustFactory.httpClient = httpClient;
    }

    public static void setTimerInterval(long timerInterval) {
        AdjustFactory.timerInterval = timerInterval;
    }

    public static void setSessionInterval(long sessionInterval) {
        AdjustFactory.sessionInterval = sessionInterval;
    }

    public static void setSubsessionInterval(long subsessionInterval) {
        AdjustFactory.subsessionInterval = subsessionInterval;
    }

}
