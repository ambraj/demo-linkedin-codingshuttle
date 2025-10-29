import api from './api';

export const postsService = {
  async createPost(postData) {
    const response = await api.post('/posts/core', postData);
    return response.data;
  },

  async getAllPosts() {
    // Correct endpoint: /posts/core/users/allPosts
    const response = await api.get('/posts/core/users/allPosts');
    return response.data;
  },

  async likePost(postId) {
    // Correct endpoint: /posts/likes/{postId}/like (not /posts/core/{postId}/like)
    const response = await api.post(`/posts/likes/${postId}/like`);
    return response.data;
  },

  async unlikePost(postId) {
    const response = await api.delete(`/posts/likes/${postId}/unlike`);
    return response.data;
  },

  async getPostById(postId) {
    const response = await api.get(`/posts/core/${postId}`);
    return response.data;
  },
};
