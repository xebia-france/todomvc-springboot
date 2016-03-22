import socketClient from 'socket.io-client'
import {receive} from 'redux/modules/rethinkdb'
import {watchTasks} from 'redux/modules/tasks'

const io = socketClient()

export function setupRealtime (store) {
  io.on('ACTION', (action) => {
    console.log('Receive action ', action)
    store.dispatch(receive(action))
    store.dispatch(action)
  })

  // Init store watchers
  store.dispatch(watchTasks())
  return io
}

export function dispatch (action) {
  console.log('Emit action', action)
  io.emit('ACTION', action)
}

export default {
  setupRealtime,
  dispatch
}
