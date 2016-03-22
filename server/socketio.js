import IO from 'koa-socket'
import {actions} from './events'

export class SocketIO {

  setup (app) {
    const io = new IO()
    io.attach(app)

    io.on('disconnect', (ctx) => {
      if (ctx.cursors) {
        ctx.cursors.forEach((cursor) => cursor.close())
      }
    })

    io.on('ACTION', (ctx, action) => {
      actions[action.type](action)((result, cursor) => {
        ctx.socket.emit('ACTION', result)
        if (cursor) {
          if (!ctx.cursors) {
            ctx.cursors = []
          }
          ctx.cursors.push(cursor)
        }
      })
    })
  }

}

export default new SocketIO()
