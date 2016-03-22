import React from 'react'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import List from 'material-ui/lib/lists/list'
import ListItem from 'material-ui/lib/lists/list-item'
import TextField from 'material-ui/lib/text-field'
import Divider from 'material-ui/lib/divider'
import {actions} from 'redux/modules/tasks'
import ActionDelete from 'material-ui/lib/svg-icons/action/delete'
import IconButton from 'material-ui/lib/icon-button'

type Props = {
  tasks: React.PropTypes.array,
  fetchTasks: React.PropTypes.func,
  addTask: React.PropTypes.func
}
export class Tasks extends React.Component {
  props: Props;

  get tasks(){
    return this.props.tasks.map((task, index) => {
      return <ListItem key={index} rightIconButton={<IconButton onTouchTap={this.handleDeleteTask}><ActionDelete /></IconButton>}>{task.name}</ListItem>
    })
  }

  componentDidMount(){
    this.props.fetchTasks()
  }

  handleAddTask = (e) => {
    if (e.keyCode === 13){
      this.props.addTask({name: e.target.value})
      e.target.value = '';
    }
  }

  handleDeleteTask = () => {
    
  }

  render() {
    return (
      <List>
        {this.tasks}
        <Divider/>
        <ListItem>
          <TextField id="newTask" fullWidth onKeyDown={this.handleAddTask}/>
        </ListItem>
      </List>
    )
  }
}

const mapStateToProps = (state) => {
  return {tasks: state.tasks}
}
const mapDispatchToProps = (dispatch) => {
  return bindActionCreators(actions, dispatch)
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Tasks)
