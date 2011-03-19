databaseChangeLog() {

//  include(path: 'changelogs', relativeToChangelog: true)


  changeSet(id:'monkey', author: 'tlberglund') {
    new File('test.sql').withPrintWriter { pw ->
      pw.println "CREATE TABLE monkey (id int, mood varchar(50))"
    }

    sqlFile(path: 'test.sql')
  }

  changeSet(author: 'tlberglund', id: 'test-load-data') {
    new File('data.csv').withPrintWriter { pw ->
      pw.println "id,mood"
      pw.println "1,angry"
      pw.println "2,happy"
      pw.println "3,ambivalent"
    }

    loadData(tableName: 'monkey', file: 'data.csv') {
      column(name: 'id', type: "NUMERIC")
      column(name: 'mood', type: "STRING")
    }
  }
  
  changeSet(author: 'tlberglund', id: 'test-delete-data') {
    delete(tableName: 'monkey') {
      where('id=2')
    }
  }

}
