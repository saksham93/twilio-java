package com.twilio.sdk.updaters;

import com.twilio.sdk.clients.TwilioRestClient;
import com.twilio.sdk.exceptions.ApiConnectionException;
import com.twilio.sdk.exceptions.ApiException;
import com.twilio.sdk.http.HttpMethod;
import com.twilio.sdk.http.Request;
import com.twilio.sdk.http.Response;
import com.twilio.sdk.resources.Call;
import com.twilio.sdk.resources.RestException;

import java.net.URI;

public class CallUpdater extends Updater<Call> {

    private final String sid;
    private URI url;
    private HttpMethod method;
    private Call.Status status;
    private URI fallbackUrl;
    private HttpMethod fallbackMethod;
    private URI statusCallback;
    private HttpMethod statusCallbackMethod;

    public CallUpdater(final String sid) {
        this.sid = sid;
    }

    public CallUpdater(final Call call) {
        this(call.getSid());
    }

    /**
     * A valid URL that returns TwiML. Twilio will immediately redirect the call
     * to the new TwiML upon execution.
     *
     * @param url URL that returns TwiML
     * @return this
     */
    public CallUpdater setUrl(final URI url) {
        this.url = url;
        return this;
    }

    /**
     * The HTTP method Twilio should use when requesting the URL. Defaults to
     * `POST`.
     *
     * @param method HTTP method to fetch TwiML with
     * @return this
     */
    public CallUpdater setMethod(final HttpMethod method) {
        this.method = method;
        return this;
    }

    /**
     * Either `canceled` or `completed`. Specifying `canceled` will attempt to
     * hangup calls that are queued or ringing but not affect calls already in
     * progress. Specifying `completed` will attempt to hang up a call even if
     * it's already in progress.
     *
     * @see com.twilio.sdk.resources.Call.Status
     * @param status Call.Status to update the Call with
     * @return this
     */
    public CallUpdater setStatus(final Call.Status status) {
        this.status = status;
        return this;
    }

    /**
     * A URL that Twilio will request if an error occurs requesting or executing
     * the TwiML at `Url`.
     * @param fallbackUrl Fallback URL in case of error
     * @return this
     */
    public CallUpdater setFallbackUrl(final URI fallbackUrl) {
        this.fallbackUrl = fallbackUrl;
        return this;
    }

    /**
     * The HTTP method that Twilio should use to request the `FallbackUrl`. Must
     * be either `GET` or `POST`. Defaults to `POST`.
     *
     * @see com.twilio.sdk.http.HttpMethod
     * @param fallbackMethod HTTP method to use with FallbackUrl
     * @return this
     */
    public CallUpdater setFallbackMethod(final HttpMethod fallbackMethod) {
        this.fallbackMethod = fallbackMethod;
        return this;
    }

    /**
     * A URL that Twilio will request when the call ends to notify your app.
     *
     * @param statusCallback Status Callback URL
     * @return this
     */
    public CallUpdater setStatusCallback(final URI statusCallback) {
        this.statusCallback = statusCallback;
        return this;
    }

    /**
     * The HTTP method Twilio should use when requesting the above URL. Defaults
     * to `POST`.
     *
     * @see com.twilio.sdk.http.HttpMethod
     * @param statusCallbackMethod HTTP method to make the request to
     *                             StatusCallback with
     * @return this
     */
    public CallUpdater setStatusCallbackMethod(final HttpMethod statusCallbackMethod) {
        this.statusCallbackMethod = statusCallbackMethod;
        return this;
    }

    @Override
    public Call execute(final TwilioRestClient client) {
        Request request = new Request(HttpMethod.POST, "/2010-04-01/Accounts/{AccountSid}/Calls/" + sid + ".json",
                                      client.getAccountSid());
        addPostParams(request);
        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("Call update failed: Unable to connect to server");
        } else if (response.getStatusCode() != TwilioRestClient.HTTP_STATUS_CODE_OK) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            throw new ApiException(restException.getMessage(), restException.getCode(), restException.getMoreInfo(),
                                   restException.getStatus(), null);
        }

        return Call.fromJson(response.getStream(), client.getObjectMapper());
    }

    private void addPostParams(final Request request) {
        if (url != null) {
            request.addPostParam("Url", url.toString());
        }

        if (method != null) {
            request.addPostParam("Method", method.toString());
        }

        if (status != null) {
            request.addPostParam("Status", status.toString());
        }

        if (fallbackUrl != null) {
            request.addPostParam("FallbackUrl", fallbackUrl.toString());
        }

        if (fallbackMethod != null) {
            request.addPostParam("FallbackMethod", fallbackMethod.toString());
        }

        if (statusCallback != null) {
            request.addPostParam("StatusCallback", statusCallback.toString());
        }

        if (statusCallbackMethod != null) {
            request.addPostParam("StatusCallbackMethod", statusCallbackMethod.toString());
        }
    }
}
