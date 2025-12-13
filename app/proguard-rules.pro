# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Regras do ProGuard para Firebase Firestore
# Mantenha os nomes de campos e métodos de seus modelos (POJOs) que são usados para serializar/desserializar dados do Firestore.
# Substitua 'com.example.project_gestoderisco.models.**' pelo caminho real dos seus modelos de dados.
-keepclassmembers class com.example.project_gestoderisco.models.** {
    public <init>();
    public *;
}