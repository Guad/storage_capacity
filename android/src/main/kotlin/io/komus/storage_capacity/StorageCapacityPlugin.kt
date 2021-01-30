package io.komus.storage_capacity

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import  android.os.StatFs
import  android.os.Environment
import  android.app.usage.StorageStatsManager
import  android.os.storage.StorageManager
import android.content.Context

/** StorageCapacityPlugin */
public class StorageCapacityPlugin: FlutterPlugin, MethodCallHandler {
  private lateinit var channel : MethodChannel
  private lateinit var context: Context
  // private lateinit var activity: Activity

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "storage_capacity")
    channel.setMethodCallHandler(this);
    context = flutterPluginBinding.applicationContext
  }

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "storage_capacity")
      channel.setMethodCallHandler(StorageCapacityPlugin())
    }
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    }
    else if(call.method == "getFreeSpace"){
      val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager;
      // val stat = StorageStatsManager()
      val bytesAvailable = storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT)
      result.success(bytesAvailable)
    }
    else if(call.method == "getOccupiedSpace"){
      val stat = StatFs(Environment.getDataDirectory().getPath())
      val bytesOccupied = stat.getTotalBytes()
      result.success(bytesOccupied)
    } else if(call.method == "getTotalSpace"){
      // val stat = StorageStatsManager()
      val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager;
      val bytesAvailable = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)
      result.success(bytesAvailable)
    }
    else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
