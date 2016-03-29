export function up (r, logger) {
  return Promise.all(['tasks'].map((tableName) => {
    logger.verbose(`Creating table ${tableName}.`)

    return r.tableCreate(tableName, {replicas: process.env.REPLICAS || 1}).run()
  })).then(() => {
    logger.verbose('Reconfigure migration table replicas')
    return r.table('_reconsider_migration').reconfigure({shards: 1, replicas: process.env.REPLICAS || 1})
  })
}

export function down (r, logger) {
  return Promise.all(['tasks'].map((tableName) => {
    logger.verbose(`Dropping table ${tableName}.`)

    return r.tableDrop(tableName).run()
  }))
}
