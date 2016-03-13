package hochschuledarmstadt.photostream_tools;

import android.content.Context;

/**
 * Created by Andreas Schattney on 08.03.2016.
 */
class GetPopularPhotosAsyncTask extends GetPhotosAsyncTask {

    public GetPopularPhotosAsyncTask(Context context, String installationId, String uri, int page, GetPhotosCallback callback) {
        super(context, installationId, uri, page, callback);
    }

    @Override
    protected String buildUrl(String uri, int page) {
        return String.format("%s/photostream/popular/?page=%s", uri, page);
    }
}