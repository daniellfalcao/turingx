plugins { id(Plugins.commonLibrary) }

dependencies {

    implementation(project(":library:core"))

    Libraries.Common(this)
    Libraries.UI(this)
}