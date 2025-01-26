import React, { useState } from 'react';
import Form from '../components/Form';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment';
import 'react-big-calendar/lib/css/react-big-calendar.css';

const localizer = momentLocalizer(moment);

const Dashboard = () => {
  const [tasks, setTasks] = useState([]);
  const [editingTask, setEditingTask] = useState(null); // State for tracking the task being edited

  const handleEditTask = (taskToEdit) => {
    setEditingTask(taskToEdit); // Set the task to be edited
  };

  const handleUpdateTask = (updatedTask) => {
    const updatedTasks = tasks.map((task) =>
      task.id === updatedTask.id ? updatedTask : task
    );
    setTasks(updatedTasks);
    setEditingTask(null); // Clear editing mode
  };

  const handleEventDrop = (event) => {
    const updatedTasks = tasks.map((task) => {
      if (task.id === event.id) {
        return {
          ...task,
          startDateTime: event.start,
          endDateTime: event.end,
        };
      }
      return task;
    });
    setTasks(updatedTasks);
  };

  return (
    <div className="min-h-screen bg-true_blue-100 p-6">
      <header className="mb-12">
        <h1 className="text-3xl font-bold text-center text-true_blue-600">Just For Today</h1>
      </header>
      <main className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <Form
          tasks={tasks}
          setTasks={setTasks}
          editingTask={editingTask} // Pass editingTask to Form
          setEditingTask={setEditingTask}
          onUpdateTask={handleUpdateTask} // Pass the update handler
        />
        <Calendar
          localizer={localizer}
          events={tasks.map((task) => ({
            id: task.id || Math.random(), // Ensure each task has a unique ID
            title: task.task,
            start: new Date(task.startDateTime),
            end: new Date(task.endDateTime),
          }))}
          startAccessor="start"
          endAccessor="end"
          style={{ height: 500, margin: '50px' }}
          onEventDrop={handleEventDrop}
        />
      </main>
    </div>
  );
};

export default Dashboard;