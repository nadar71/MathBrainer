# Dependencies provide their own consumer rules. Preserve only the metadata needed
# to turn obfuscated release stack traces into actionable source locations.
-keepattributes SourceFile,LineNumberTable

# AndroidJUnitRunner loads this target-APK dependency during minified release smoke tests.
-keep class androidx.tracing.Trace { *; }
