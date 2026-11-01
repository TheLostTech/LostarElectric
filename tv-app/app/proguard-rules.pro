# kotlinx.serialization ships its own R8 rules; keep our @Serializable models' companions just in case.
-keepclassmembers @kotlinx.serialization.Serializable class com.lostar.tv.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
