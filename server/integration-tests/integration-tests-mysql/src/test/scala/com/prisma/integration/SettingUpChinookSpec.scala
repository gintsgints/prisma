package com.prisma.integration

import com.prisma.api.import_export.BulkImport
import org.scalatest.{FlatSpec, Matchers}
import java.io.FileInputStream

import play.api.libs.json.Json

class SettingUpChinookSpec extends FlatSpec with Matchers with IntegrationBaseSpec {

  "Importing Chinook" should "work " in {

    val schema =
      """type Artist {
        |  id: ID! @unique
        |  ArtistId: Int! @unique
        |  Name: String!
        |  Albums: [Album!]!
        |}
        |
        |type Album {
        |  id: ID! @unique
        |  AlbumId: Int! @unique
        |  Title: String!
        |  Artist: Artist!
        |  Tracks: [Track!]!
        |}
        |
        |type Genre {
        |  id: ID! @unique
        |  GenreId: Int! @unique
        |  Name: String!
        |  Tracks: [Track!]!
        |}
        |
        |type MediaType {
        |  id: ID! @unique
        |  MediaTypeId: Int! @unique
        |  Name: String!
        |  Tracks: [Track!]!
        |}
        |
        |type Track {
        |  id: ID! @unique
        |  TrackId: Int! @unique
        |  Name: String!
        |  Album: Album!
        |  MediaType: MediaType!
        |  Genre: Genre!
        |  Composer: String
        |  Milliseconds: Int!
        |  Bytes: Int!
        |  UnitPrice: Float!
        |}
        |"""

    val (project, _) = setupProject(schema)

    val importer = new BulkImport(project)

    def importFile(fileName: String) = {
      val path   = "/Users/matthias/repos/github.com/graphcool/framework/server/integration-tests/integration-tests-mysql/src/test/scala/com/prisma/integration/" + fileName
      val stream = new FileInputStream(path)
      val json   = try { Json.parse(stream) } finally { stream.close() }
      importer.executeImport(json).await(50)
    }

//    importFile("nodes01.json")
//    importFile("nodes02.json")
//    importFile("relations01.json")
//    importFile("relations02.json")
//    importFile("lists01.json")

    def runquery = {
      val starttime = System.currentTimeMillis()

      //    apiServer.query("""query{artists(where:{Albums_some:{Tracks_some:{Milliseconds_gt: 500000}}}){Name}}""", project)
      //    apiServer.query("""query{artists(where:{Albums_some:{Title_starts_with: "B" Title_ends_with:"C"}}){Name}}""", project)
      apiServer.query(
        """query prisma_deeplyNested {albums(where: {Tracks_some:{ MediaType:{Name_starts_with:""}, Genre:{Name_starts_with:""}}}) { id}}""",
        project
      )

      val endtime = System.currentTimeMillis()

      println("duration: " + (endtime - starttime))
    }

    runquery

//    for (a <- 1 to 50) {
//      runquery
//    }
  }
}
