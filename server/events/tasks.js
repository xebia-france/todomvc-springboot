import RethinkDB from '../rethinkdb'
import { FETCH_TASKS, WATCH_TASKS, UPDATE_TASK, ADD_TASK } from '../../src/constants/tasks'

export const fetchTasks = (action): Function => {
  return (dispatch: Function): Promise => {
    return RethinkDB.execute((r, conn) => {
      return r.table('tasks')
        .run(conn)
        .then((cursor) => cursor.toArray().then((result) => {
          dispatch({
            type: FETCH_TASKS,
            payload: result
          })
        }))
    })
  }
}

export const watchTasks = (action): Function => {
  return (dispatch: Function): Promise => {
    return RethinkDB.execute((r, conn) => {
      return r.table('tasks').changes().run(conn).then((cursor) => {
        cursor.each((error, change) => {
          if (error) return console.error(error.stack)
          dispatch({
            type: UPDATE_TASK,
            payload: change.new_val
          }, cursor)
        })
      })
    })
  }
}

export const actions = {
  [FETCH_TASKS]: fetchTasks,
  [WATCH_TASKS]: watchTasks
}
