import React, { useState, useEffect } from 'react';

const prioritizeTasks = (tasks) => {
    return tasks.sort((a, b) => {
      const priorityOrder = { 'Need': 1, 'Want': 2 };
      const impactOrder = { 'High': 1, 'Medium': 2, 'Low': 3 };
      return (
        priorityOrder[a.category] - priorityOrder[b.category] ||
        impactOrder[a.impact] - impactOrder[b.impact]
      );
    });
  };

const Form = ({ tasks, setTasks, editingTask, setEditingTask, onUpdateTask }) => {
  const [task, setTask] = useState('');
  const [frequency, setFrequency] = useState('');
  const [duration, setDuration] = useState('');
  const [intensity, setIntensity] = useState('');
  const [category, setCategory] = useState('');
  const [impact, setImpact] = useState('');
  const [startDate, setStartDate] = useState('');
  const [startTime, setStartTime] = useState('');
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (editingTask) {
      setTask(editingTask.task);
      setFrequency(editingTask.frequency);
      setDuration(editingTask.duration);
      setIntensity(editingTask.intensity);
      setCategory(editingTask.category);
      setImpact(editingTask.impact);
      setStartDate(new Date(editingTask.startDateTime).toISOString().split('T')[0]);
      setStartTime(new Date(editingTask.startDateTime).toTimeString().slice(0, 5)); // Extract HH:MM
    } else {
      // Clear form if not editing
      setTask('');
      setFrequency('');
      setDuration('');
      setIntensity('');
      setCategory('');
      setImpact('');
      setStartDate('');
      setStartTime('');
    }
  }, [editingTask]); // Update form fields when editingTask changes

  const validateForm = () => {
    const newErrors = {};
    if (!task) newErrors.task = 'Task is required';
    if (!frequency) newErrors.frequency = 'Frequency is required';
    if (!duration) newErrors.duration = 'Duration is required';
    if (!intensity) newErrors.intensity = 'Intensity is required';
    if (!category) newErrors.category = 'Category is required';
    if (!impact) newErrors.impact = 'Impact is required';
    if (!startDate) newErrors.startDate = 'Start date is required';
    if (!startTime) newErrors.startTime = 'Start time is required';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleEditTask = (task) => {
    setEditingTask(task);
  };

  const handleSubmit = (e) => {
    e.preventDefault(); // Prevent form submission

    if (!validateForm()) return;

    const startDateTime = new Date(`${startDate}T${startTime}`);
    const endDateTime = new Date(startDateTime.getTime() + duration * 60000);

    const newTask = {
      id: editingTask ? editingTask.id : Math.random(), // Preserve ID or generate new
      task,
      frequency,
      duration,
      intensity,
      category,
      impact,
      startDateTime,
      endDateTime,
    };

    if (editingTask) {
      onUpdateTask(newTask); // Call the update handler in the parent
    } else {
      setTasks((prevTasks) => {
        const updatedTasks = prioritizeTasks([...prevTasks, newTask]);
        return updatedTasks;
      });
    }

    // Clear form fields only if NOT editing
    if (!editingTask) {
      setTask('');
      setFrequency('');
      setDuration('');
      setIntensity('');
      setCategory('');
      setImpact('');
      setStartDate('');
      setStartTime('');
    }

    setEditingTask(null); // Clear editing task after submit
  };

  return (
    <div className="p-6 max-w-lg mx-auto bg-white rounded-xl shadow-lg space-y-4">
     <h2 className="text-2xl font-bold text-true_blue-500">Add Task</h2>
      <div className="space-y-2">
        <input
          type="text"
          placeholder="Task"
          value={task}
          onChange={(e) => setTask(e.target.value)}
          className={`w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-true_blue-500 ${errors.task ? 'border-red-500' : ''}`}
        />
        {errors.task && <p className="text-red-500 text-sm">{errors.task}</p>}

        <input
          type="text"
          placeholder="Frequency (e.g., daily, every two days)"
          value={frequency}
          onChange={(e) => setFrequency(e.target.value)}
          className={`w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-true_blue-500 ${errors.frequency ? 'border-red-500' : ''}`}
        />
        {errors.frequency && <p className="text-red-500 text-sm">{errors.frequency}</p>}

        <input
          type="number"
          placeholder="Duration (in minutes)"
          value={duration}
          onChange={(e) => setDuration(e.target.value)}
          className={`w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-true_blue-500 ${errors.duration ? 'border-red-500' : ''}`}
        />
        {errors.duration && <p className="text-red-500 text-sm">{errors.duration}</p>}

        <input
          type="number"
          placeholder="Intensity (1-10)"
          value={intensity}
          onChange={(e) => setIntensity(e.target.value)}
          className={`w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-true_blue-500 ${errors.intensity ? 'border-red-500' : ''}`}
        />
        {errors.intensity && <p className="text-red-500 text-sm">{errors.intensity}</p>}

        <input
          type="text"
          placeholder="Category (Need or Want)"
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          className={`w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-true_blue-500 ${errors.category ? 'border-red-500' : ''}`}
        />
        {errors.category && <p className="text-red-500 text-sm">{errors.category}</p>}

        <input
          type="text"
          placeholder="Impact (High/Medium/Low)"
          value={impact}
          onChange={(e) => setImpact(e.target.value)}
          className={`w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-true_blue-500 ${errors.impact ? 'border-red-500' : ''}`}
        />
        {errors.impact && <p className="text-red-500 text-sm">{errors.impact}</p>}

        <input
          type="date"
          placeholder="Start Date"
          value={startDate}
          onChange={(e) => setStartDate(e.target.value)}
          className={`w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-true_blue-500 ${errors.startDate ? 'border-red-500' : ''}`}
        />
        {errors.startDate && <p className="text-red-500 text-sm">{errors.startDate}</p>}

        <input
          type="time"
          placeholder="Start Time"
          value={startTime}
          onChange={(e) => setStartTime(e.target.value)}
          className={`w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-true_blue-500 ${errors.startTime ? 'border-red-500' : ''}`}
        />
        {errors.startTime && <p className="text-red-500 text-sm">{errors.startTime}</p>}
      </div>

      <button type="submit" onClick={handleSubmit} className="w-full py-2 bg-atomic_tangerine-500 text-white rounded-md hover:bg-atomic_tangerine-700 focus:outline-none focus:ring-2 focus:ring-atomic_tangerine-500">
        {editingTask ? 'Update Task' : 'Add Task'}
      </button>

      <div className="space-y-2">
        {tasks.map((t) => (
          <div key={t.id} className="p-4 bg-gray-100 rounded-md shadow flex items-center justify-between">
            <div>
            <p><strong>Task:</strong> {t.task}</p>
            <p><strong>Frequency:</strong> {t.frequency}</p>
            <p><strong>Duration:</strong> {t.duration} minutes</p>
            <p><strong>Intensity:</strong> {t.intensity}</p>
            <p><strong>Category:</strong> {t.category}</p>
            <p><strong>Impact:</strong> {t.impact}</p>
            <p><strong>Start Date:</strong> {new Date(t.startDateTime).toLocaleDateString()}</p>
            <p><strong>Start Time:</strong> {new Date(t.startDateTime).toLocaleTimeString()}</p>
            </div>
            <button
              onClick={() => handleEditTask(t)} // Pass the task object for editing
              className="bg-yellow-500 hover:bg-yellow-700 text-white font-bold py-2 px-4 rounded"
            >
              Edit
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Form;