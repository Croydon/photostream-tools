/*
 * The MIT License
 *
 * Copyright (c) 2016 Andreas Schattney
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package hochschuledarmstadt.photostream_tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class UrlBuilder {

    private final String baseUrl;

    public UrlBuilder(String baseUrl){
        this.baseUrl = baseUrl;
    }

    public String getUploadPhotoApiUrl(){
        return  String.format("%s/photostream/api/image", baseUrl);
    }

    public String getLoadPhotosApiUrl(){
        return String.format("%s/photostream/api/stream", baseUrl);
    }

    public String getLoadMorePhotosApiUrl(){
        return String.format("%s/photostream/api/stream/more", baseUrl);
    }

    public String getDeletePhotoApiUrl(int photoId){
        return String.format("%s/photostream/api/image/%s", baseUrl, photoId);
    }

    public String getUploadCommentApiUrl(int photoId){
        return String.format("%s/photostream/api/image/%s/comment", baseUrl, photoId);
    }

    public String getLoadCommentsApiUrl(int photoId){
        return String.format("%s/photostream/api/image/%s/comments", baseUrl, photoId);
    }

    public String getDeleteCommentApiUrl(int commentId){
        return String.format("%s/photostream/api/comment/%s", baseUrl, commentId);
    }

    public String getLikePhotoApiUrl(int photoId){
        return String.format("%s/photostream/api/image/%s/like", baseUrl, photoId);
    }

    public String getResetLikeForPhotoApiUrl(int photoId){
        return String.format("%s/photostream/api/image/%s/dislike", baseUrl, photoId);
    }

    public String getSearchMorePhotosApiUrl() {
        return String.format("%s/photostream/api/search/more", baseUrl);
    }

    public String getSearchPhotosApiUrl(String query) {
        try {
            return String.format("%s/photostream/api/search/?q=%s", baseUrl, URLEncoder.encode(query, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return String.format("%s/photostream/api/search/?q=%s", baseUrl, query);
        }
    }

    public String getFormatPhotoContentApiUrl(){
        return String.format("%s/photostream/api/image/%s/content", baseUrl, "%s");
    }

}
