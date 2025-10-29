import { useState } from 'react';
import { postsService } from '../services/postsService';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';

const CreatePost = ({ onPostCreated }) => {
  const [content, setContent] = useState('');
  const [visibility, setVisibility] = useState('PUBLIC');
  const [loading, setLoading] = useState(false);
  const { user } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!content.trim()) {
      toast.error('Please write something');
      return;
    }

    setLoading(true);
    try {
      const newPost = await postsService.createPost({
        content: content.trim(),
        visibility,
        authorId: user.id
      });
      toast.success('Post created successfully!');
      setContent('');
      if (onPostCreated) {
        onPostCreated(newPost);
      }
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to create post');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow p-4 mb-4">
      <form onSubmit={handleSubmit}>
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="What do you want to talk about?"
          className="w-full p-3 border border-gray-300 rounded-lg resize-none focus:ring-2 focus:ring-linkedin-blue focus:border-transparent outline-none"
          rows="4"
        />
        
        <div className="flex items-center justify-between mt-3">
          <select
            value={visibility}
            onChange={(e) => setVisibility(e.target.value)}
            className="px-3 py-1.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-linkedin-blue focus:border-transparent outline-none text-sm"
          >
            <option value="PUBLIC">Public</option>
            <option value="CONNECTIONS">Connections only</option>
          </select>
          
          <button
            type="submit"
            disabled={loading || !content.trim()}
            className="px-6 py-2 bg-linkedin-blue text-white rounded-full font-semibold hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? 'Posting...' : 'Post'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreatePost;
