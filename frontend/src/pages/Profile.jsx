import { useState, useEffect } from 'react';
import Layout from '../components/Layout';
import Loader from '../components/Loader';
import PostCard from '../components/PostCard';
import { useAuth } from '../context/AuthContext';
import { postsService } from '../services/postsService';
import { connectionsService } from '../services/connectionsService';
import { Mail, Users } from 'lucide-react';

const Profile = () => {
  const { user } = useAuth();
  const [userPosts, setUserPosts] = useState([]);
  const [connections, setConnections] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('posts');

  useEffect(() => {
    fetchUserData();
  }, []);

  const fetchUserData = async () => {
    try {
      // Backend endpoint: /posts/core/users/allPosts (returns all posts for authenticated user)
      const postsData = await postsService.getAllPosts();
      setUserPosts(Array.isArray(postsData) ? postsData : []);
      
      const connectionsData = await connectionsService.getConnections();
      setConnections(connectionsData);
    } catch (error) {
      console.error('Failed to load profile data');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="max-w-4xl mx-auto">
        {/* Profile Header */}
        <div className="bg-white rounded-lg shadow mb-4 overflow-hidden">
          <div className="h-32 bg-gradient-to-r from-linkedin-blue to-blue-400"></div>
          <div className="px-6 pb-6">
            <div className="flex flex-col sm:flex-row items-start sm:items-end -mt-16 sm:-mt-12">
              <div className="w-24 h-24 sm:w-32 sm:h-32 rounded-full bg-white border-4 border-white shadow-lg flex items-center justify-center text-4xl font-bold text-linkedin-blue">
                {user?.name?.charAt(0)?.toUpperCase()}
              </div>
              <div className="mt-4 sm:mt-0 sm:ml-6 flex-1">
                <h1 className="text-2xl font-bold text-gray-900">{user?.name}</h1>
                <div className="flex items-center space-x-4 mt-2 text-gray-600">
                  <div className="flex items-center space-x-1">
                    <Mail size={16} />
                    <span className="text-sm">{user?.email}</span>
                  </div>
                  <div className="flex items-center space-x-1">
                    <Users size={16} />
                    <span className="text-sm">{connections.length} connections</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="bg-white rounded-lg shadow mb-4">
          <div className="flex border-b">
            <button
              onClick={() => setActiveTab('posts')}
              className={`flex-1 px-6 py-3 font-semibold ${
                activeTab === 'posts'
                  ? 'text-linkedin-blue border-b-2 border-linkedin-blue'
                  : 'text-gray-600'
              }`}
            >
              Posts
            </button>
            <button
              onClick={() => setActiveTab('connections')}
              className={`flex-1 px-6 py-3 font-semibold ${
                activeTab === 'connections'
                  ? 'text-linkedin-blue border-b-2 border-linkedin-blue'
                  : 'text-gray-600'
              }`}
            >
              Connections
            </button>
          </div>
        </div>

        {/* Content */}
        {loading ? (
          <Loader />
        ) : (
          <>
            {activeTab === 'posts' && (
              <div>
                {userPosts.length === 0 ? (
                  <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
                    No posts yet
                  </div>
                ) : (
                  userPosts.map((post) => <PostCard key={post.id} post={post} />)
                )}
              </div>
            )}

            {activeTab === 'connections' && (
              <div className="bg-white rounded-lg shadow p-4">
                {connections.length === 0 ? (
                  <p className="text-center text-gray-500 py-8">No connections yet</p>
                ) : (
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    {connections.map((connection) => (
                      <div
                        key={connection.id}
                        className="flex items-center space-x-3 p-3 hover:bg-gray-50 rounded-lg"
                      >
                        <div className="w-12 h-12 rounded-full bg-linkedin-blue flex items-center justify-center text-white font-semibold">
                          {connection.name?.charAt(0)?.toUpperCase()}
                        </div>
                        <div>
                          <h3 className="font-semibold">{connection.name}</h3>
                          <p className="text-sm text-gray-500">{connection.email}</p>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}
          </>
        )}
      </div>
    </Layout>
  );
};

export default Profile;
