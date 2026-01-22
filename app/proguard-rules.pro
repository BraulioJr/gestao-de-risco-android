# Adicione regras específicas do ProGuard para o projeto aqui.
# Você pode controlar o conjunto de arquivos de configuração aplicados usando a
# configuração proguardFiles no build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Se o seu projeto usa WebView com JS, descomente o seguinte
# e especifique o nome de classe totalmente qualificado para a classe
# de interface JavaScript:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Descomente isto para preservar as informações de número de linha para
# rastreamentos de pilha de depuração (stack traces).
#-keepattributes SourceFile,LineNumberTable

# Se você mantiver as informações de número de linha, descomente isto para
# ocultar o nome do arquivo de origem original.
#-renamesourcefileattribute SourceFile

# Regras do ProGuard para Firebase Firestore
# Mantenha os nomes de campos e métodos de seus modelos (POJOs) que são usados para serializar/desserializar dados do Firestore.
# Substitua 'com.example.gestaoderisco.models' pelo pacote real dos seus modelos de dados.
# O ** no final garante que todas as classes e subclasses dentro do pacote 'models' sejam incluídas.
-keepclassmembers class com.example.gestaoderisco.models.** {
    public <init>();
    public *;
}