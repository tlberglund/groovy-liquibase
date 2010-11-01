databaseChangeLog() {

  changeSet(author: 'tlberglund', id: 'change-set-001') {
    comment "This is a comment"
    renameTable(schemaName: 'not-used', oldTableName: 'monkey', newTableName: 'users')
  }

}
