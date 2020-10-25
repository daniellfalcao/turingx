plugins { id(Plugins.commonLibrary) }

dependencies {

    implementation(project(":library:core"))
    implementation(project(":library:lifecycle"))

    Libraries.Common(this)
    Libraries.UI(this)
}
