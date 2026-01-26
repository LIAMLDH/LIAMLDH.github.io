import { defineStore } from 'pinia'

export const useStudentStore = defineStore('student', {
  state: () => ({
    students: [],
    courses: [],
    currentPage: 1,
    pageSize: 10,
    totalStudents: 0
  }),

  getters: {
    getStudents: (state) => state.students,
    getCourses: (state) => state.courses,
    getPaginationInfo: (state) => ({
      currentPage: state.currentPage,
      pageSize: state.pageSize,
      totalStudents: state.totalStudents
    })
  },

  actions: {
    setStudents(students) {
      this.students = students
    },

    addStudent(student) {
      this.students.push(student)
    },

    updateStudent(updatedStudent) {
      const index = this.students.findIndex(s => s.id === updatedStudent.id)
      if (index !== -1) {
        this.students[index] = updatedStudent
      }
    },

    removeStudent(studentId) {
      this.students = this.students.filter(s => s.id !== studentId)
    },

    setCourses(courses) {
      this.courses = courses
    },

    setPageInfo({ currentPage, pageSize, totalStudents }) {
      this.currentPage = currentPage
      this.pageSize = pageSize
      this.totalStudents = totalStudents
    }
  },

  persist: true
})