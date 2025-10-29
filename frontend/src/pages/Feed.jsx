import { useState, useEffect } from 'react';
import Layout from '../components/Layout';
import CreatePost from '../components/CreatePost';
import PostCard from '../components/PostCard';
import Loader from '../components/Loader';
import { postsService } from '../services/postsService';
import toast from 'react-hot-toast';

const Feed = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchPosts = async () => {
    try {
      // Backend endpoint: /posts/core/users/allPosts (returns all posts for authenticated user)
      const data = await postsService.getAllPosts();
      setPosts(Array.isArray(data) ? data : []);
    } catch (error) {
      toast.error('Failed to load posts');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPosts();
  }, []);

  const handlePostCreated = (newPost) => {
    setPosts(prev => [newPost, ...prev]);
  };

  return (
    <Layout>
      <div className="max-w-2xl mx-auto">
        <CreatePost onPostCreated={handlePostCreated} />
        
        {loading ? (
          <Loader />
        ) : (
          <>
            {posts.length === 0 ? (
              <div className="bg-white rounded-lg shadow p-8 text-center">
                <p className="text-gray-500">No posts yet. Be the first to share something!</p>
              </div>
            ) : (
              <>
                {posts.map((post) => (
                  <PostCard key={post.id} post={post} />
                ))}
              </>
            )}
          </>
        )}
      </div>
    </Layout>
  );
};

export default Feed;
