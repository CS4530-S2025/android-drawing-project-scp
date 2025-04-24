#include <jni.h>
#include <android/bitmap.h>
#include <cstdlib>
#include <ctime>
#include <algorithm>
#include <android/log.h>
#define LOG_TAG "NativeFilters"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL
Java_com_example_drawingapp_NativeImageFilters_invertColors(JNIEnv* env, jobject, jobject bitmap) {
AndroidBitmapInfo info;
void* pixels;
    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) return;
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGD("Unsupported bitmap format: %d", info.format);
        return;
    }

    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0) return;

    uint32_t* line = (uint32_t*)pixels;
    for (int y = 0; y < info.height; ++y) {
        for (int x = 0; x < info.width; ++x) {
            uint32_t pixel = line[y * info.width + x];

            uint8_t a = (pixel >> 24) & 0xFF;
            uint8_t r = 255 - ((pixel >> 16) & 0xFF);
            uint8_t g = 255 - ((pixel >> 8) & 0xFF);
            uint8_t b = 255 - (pixel & 0xFF);

            line[y * info.width + x] = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_drawingapp_NativeImageFilters_addNoise(JNIEnv* env, jobject, jobject bitmap) {
    srand(time(0));
    AndroidBitmapInfo info;
    void* pixels;

    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) return;
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) return;
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0) return;

    uint32_t* line = (uint32_t*)pixels;
    for (int y = 0; y < info.height; ++y) {
        for (int x = 0; x < info.width; ++x) {
            uint32_t pixel = line[y * info.width + x];

            uint8_t a = (pixel >> 24) & 0xFF;
            uint8_t r = (pixel >> 16) & 0xFF;
            uint8_t g = (pixel >> 8) & 0xFF;
            uint8_t b = pixel & 0xFF;

            int noise = rand() % 150 - 75;
            r = std::min(255, std::max(0, r + noise));
            g = std::min(255, std::max(0, g + noise));
            b = std::min(255, std::max(0, b + noise));

            line[y * info.width + x] = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}
