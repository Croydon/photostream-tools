package hochschuledarmstadt.photostream_tools.service;

import android.content.Context;

/**
 * Created by Andreas Schattney on 08.03.2016.
 */
public class GetPopularPhotosAsyncTask extends GetStreamAsyncTask {

    public GetPopularPhotosAsyncTask(Context context, String installationId, String uri, int page, StreamCallback callback) {
        super(context, installationId, uri, page, callback);
    }

    @Override
    protected String buildUrl(String uri, int page) {
        final String url = String.format("%s/photostream/popular/?page=%s", uri, page);
        return url;
    }
}
