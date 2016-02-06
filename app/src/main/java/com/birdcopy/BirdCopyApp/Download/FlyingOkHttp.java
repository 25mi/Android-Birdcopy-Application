package com.birdcopy.BirdCopyApp.Download;

/**
 * Created by vincentsung on 1/23/16.
 */

import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public final class FlyingOkHttp {

	private static OkHttpClient client = new OkHttpClient();

	public interface ProgressListener {
		void update(long bytesRead, long contentLength, boolean done);
	}

	public interface DownloadFileOKListener {

		void completion(final boolean isOK,final  String targetpath);
	}

	public static void downloadFile(final String url,
	                                final String targetpath,
	                                final ProgressListener progeressDelegate,
	                                final DownloadFileOKListener okDelegate)
	{
		Request request = new Request.Builder()
				.url(url)
				.build();

		client.newBuilder()
				.addNetworkInterceptor(new Interceptor() {
					@Override
					public Response intercept(Chain chain) throws IOException {
						Response originalResponse = chain.proceed(chain.request());
						return originalResponse.newBuilder()
								.body(new ProgressResponseBody(originalResponse.body(), progeressDelegate))
								.build();
					}
				})
				.build();

		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {

				Log.e("downloadFile", e.getMessage());
			}

			@Override
			public void onResponse(Response response) throws IOException {

				if (response.isSuccessful()) {

					if(targetpath!=null)
					{
						File targetFile = FlyingFileManager.getFile(targetpath);

						BufferedSink sink = Okio.buffer(Okio.sink(targetFile));
						sink.writeAll(response.body().source());
						sink.close();

						if(okDelegate!=null)
						{
							okDelegate.completion(true,targetpath);
						}
					}
				}
				else {

					if(okDelegate!=null)
					{
						okDelegate.completion(false,targetpath);
					}
				}

				response.body().close();
			}
		});
	}

	private static class ProgressResponseBody extends ResponseBody {

		private final ResponseBody responseBody;
		private final ProgressListener progressListener;
		private BufferedSource bufferedSource;

		public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
			this.responseBody = responseBody;
			this.progressListener = progressListener;
		}

		@Override public MediaType contentType() {
			return responseBody.contentType();
		}

		@Override public long contentLength() {
			return responseBody.contentLength();
		}

		@Override public BufferedSource source() {
			if (bufferedSource == null) {
				bufferedSource = Okio.buffer(source(responseBody.source()));
			}
			return bufferedSource;
		}

		private Source source(Source source) {
			return new ForwardingSource(source) {
				long totalBytesRead = 0L;

				@Override public long read(Buffer sink, long byteCount) throws IOException {
					long bytesRead = super.read(sink, byteCount);
					// read() returns the number of bytes read, or -1 if this source is exhausted.
					totalBytesRead += bytesRead != -1 ? bytesRead : 0;
					progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
					return bytesRead;
				}
			};
		}
	}
}
