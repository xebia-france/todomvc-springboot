import r from 'rethinkdb'
import fs from 'fs'
import path from 'path'
import * as serverInfo from './constants'

export class RethinkDB {

  init (conn) {
    console.log('RethinkDB migration')
    r.dbList().run(conn, (err, dbs) => {
      if (!dbs.includes('todo')) {
        console.log('Creating toolbox database')
        r.dbCreate('todo').run(conn)
      }
      r.tableList().run(conn, (err, result) => {
        ['tasks'].forEach((table) => {
          if (!result.includes(table)) {
            console.log('Creating table', table)
            r.tableCreate(table).run(conn)
          }
        })
      })
    })
    console.log('RethinkDB migration done')
  }

  execute (cb) {
    return cb(r, this.connection)
  }

  connect () {
    return r.connect({
      host: '172.16.39.128',
      port: 32772,
      db: 'todo'
    }).then((connection) => {
      this.connection = connection
      console.log('RethinkDB connected')
      this.init(connection)
    })
  }

}

export default new RethinkDB()
