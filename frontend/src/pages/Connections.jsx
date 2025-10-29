import { useState, useEffect, useRef } from 'react';
import Layout from '../components/Layout';
import Loader from '../components/Loader';
import { connectionsService } from '../services/connectionsService';
import { UserPlus, UserCheck, X, Check, MoreVertical, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';

const Connections = () => {
  const [connections, setConnections] = useState([]);
  const [pendingRequests, setPendingRequests] = useState([]);
  const [suggestedConnections, setSuggestedConnections] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('connections');
  const [openMenuId, setOpenMenuId] = useState(null);
  const menuRefs = useRef({});

  useEffect(() => {
    fetchConnections();
    fetchPendingRequests();
    fetchSuggestedConnections();
  }, []);

  const fetchConnections = async () => {
    try {
      const data = await connectionsService.getConnections();
      setConnections(data);
    } catch (error) {
      toast.error('Failed to load connections');
    } finally {
      setLoading(false);
    }
  };

  const fetchPendingRequests = async () => {
    try {
      const data = await connectionsService.getReceivedRequests();
      setPendingRequests(data);
    } catch (error) {
      console.error('Failed to load pending requests');
    }
  };

  const fetchSuggestedConnections = async () => {
    try {
      const data = await connectionsService.getSuggestedConnections();
      setSuggestedConnections(data);
    } catch (error) {
      console.error('Failed to load suggested connections');
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchQuery.trim()) return;

    try {
      const data = await connectionsService.searchUsers(searchQuery);
      setSearchResults(data);
      setActiveTab('search');
    } catch (error) {
      toast.error('Search failed');
    }
  };

  const handleConnect = async (userId) => {
    try {
      await connectionsService.sendConnectionRequest(userId);
      toast.success('Connection request sent!');
      setSearchResults(prev => prev.filter(user => user.id !== userId));
      setSuggestedConnections(prev => prev.filter(user => user.id !== userId));
      // Refresh suggested connections list after sending a request
      fetchSuggestedConnections();
    } catch (error) {
      toast.error('Failed to send request');
    }
  };

  const handleAccept = async (userId) => {
    try {
      await connectionsService.acceptConnectionRequest(userId);
      toast.success('Connection accepted!');
      fetchConnections();
      fetchPendingRequests();
    } catch (error) {
      toast.error('Failed to accept request');
    }
  };

  const handleReject = async (userId) => {
    try {
      await connectionsService.rejectConnectionRequest(userId);
      toast.success('Request rejected');
      fetchPendingRequests();
      // Refresh suggested connections list after rejecting a request
      fetchSuggestedConnections();
    } catch (error) {
      toast.error('Failed to reject request');
    }
  };

  const handleRemoveConnection = async (userId) => {
    try {
      await connectionsService.removeConnection(userId);
      toast.success('Connection removed');
      setOpenMenuId(null);
      fetchConnections();
    } catch (error) {
      toast.error('Failed to remove connection');
    }
  };

  return (
    <Layout>
      <div className="max-w-4xl mx-auto">
        {/* Tabs */}
        <div className="bg-white rounded-lg shadow mb-4">
          <div className="flex border-b">
            <button
              onClick={() => setActiveTab('connections')}
              className={`flex-1 px-4 py-3 font-semibold ${
                activeTab === 'connections'
                  ? 'text-linkedin-blue border-b-2 border-linkedin-blue'
                  : 'text-gray-600'
              }`}
            >
              My Connections ({connections.length})
            </button>
            <button
              onClick={() => setActiveTab('requests')}
              className={`flex-1 px-4 py-3 font-semibold ${
                activeTab === 'requests'
                  ? 'text-linkedin-blue border-b-2 border-linkedin-blue'
                  : 'text-gray-600'
              }`}
            >
              Requests ({pendingRequests.length})
            </button>
            <button
              onClick={() => setActiveTab('suggested')}
              className={`flex-1 px-4 py-3 font-semibold ${
                activeTab === 'suggested'
                  ? 'text-linkedin-blue border-b-2 border-linkedin-blue'
                  : 'text-gray-600'
              }`}
            >
              Suggested ({suggestedConnections.length})
            </button>
          </div>
        </div>

        {loading ? (
          <Loader />
        ) : (
          <div className="bg-white rounded-lg shadow">
            {/* Connections Tab */}
            {activeTab === 'connections' && (
              <div className="p-4">
                {connections.length === 0 ? (
                  <p className="text-center text-gray-500 py-8">No connections yet</p>
                ) : (
                  <div className="space-y-3">
                    {connections.map((connection) => (
                      <div key={connection.id} className="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg group">
                        <div className="flex items-center space-x-3">
                          <div className="w-12 h-12 rounded-full bg-linkedin-blue flex items-center justify-center text-white font-semibold">
                            {connection.name?.charAt(0)?.toUpperCase()}
                          </div>
                          <div>
                            <h3 className="font-semibold">{connection.name}</h3>
                            <p className="text-sm text-gray-500">{connection.email}</p>
                          </div>
                        </div>
                        <div className="flex items-center space-x-3">
                          <UserCheck className="text-green-600" size={20} />
                          {/* Three-dots menu */}
                          <div className="relative">
                            <button
                              onClick={() => setOpenMenuId(openMenuId === connection.id ? null : connection.id)}
                              className="p-2 hover:bg-gray-200 rounded-full transition opacity-0 group-hover:opacity-100"
                              title="More options"
                            >
                              <MoreVertical size={20} className="text-gray-600" />
                            </button>
                            {/* Dropdown Menu */}
                            {openMenuId === connection.id && (
                              <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg z-10 border border-gray-200">
                                <button
                                  onClick={() => handleRemoveConnection(connection.userId)}
                                  className="w-full text-left px-4 py-2 hover:bg-gray-100 rounded-lg flex items-center space-x-2 text-red-600 hover:text-red-700 transition"
                                >
                                  <Trash2 size={18} />
                                  <span>Remove Connection</span>
                                </button>
                              </div>
                            )}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* Requests Tab */}
            {activeTab === 'requests' && (
              <div className="p-4">
                {pendingRequests.length === 0 ? (
                  <p className="text-center text-gray-500 py-8">No pending requests</p>
                ) : (
                  <div className="space-y-3">
                    {pendingRequests.map((request) => (
                      <div key={request.id} className="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg">
                        <div className="flex items-center space-x-3">
                          <div className="w-12 h-12 rounded-full bg-linkedin-blue flex items-center justify-center text-white font-semibold">
                            {request.name?.charAt(0)?.toUpperCase()}
                          </div>
                          <div>
                            <h3 className="font-semibold">{request.name}</h3>
                            <p className="text-sm text-gray-500">{request.email}</p>
                          </div>
                        </div>
                        <div className="flex space-x-2">
                          <button
                            onClick={() => handleAccept(request.userId)}
                            className="p-2 bg-linkedin-blue text-white rounded-full hover:bg-blue-700 transition"
                          >
                            <Check size={20} />
                          </button>
                          <button
                            onClick={() => handleReject(request.userId)}
                            className="p-2 bg-gray-200 text-gray-700 rounded-full hover:bg-gray-300 transition"
                          >
                            <X size={20} />
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* Search Results Tab */}
            {activeTab === 'search' && (
              <div className="p-4">
                {searchResults.length === 0 ? (
                  <p className="text-center text-gray-500 py-8">No results found</p>
                ) : (
                  <div className="space-y-3">
                    {searchResults.map((user) => (
                      <div key={user.id} className="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg">
                        <div className="flex items-center space-x-3">
                          <div className="w-12 h-12 rounded-full bg-linkedin-blue flex items-center justify-center text-white font-semibold">
                            {user.name?.charAt(0)?.toUpperCase()}
                          </div>
                          <div>
                            <h3 className="font-semibold">{user.name}</h3>
                            <p className="text-sm text-gray-500">{user.email}</p>
                          </div>
                        </div>
                        <button
                          onClick={() => handleConnect(user.userId)}
                          className="flex items-center space-x-2 px-4 py-2 bg-linkedin-blue text-white rounded-full hover:bg-blue-700 transition"
                        >
                          <UserPlus size={18} />
                          <span>Connect</span>
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* Suggested Connections Tab */}
            {activeTab === 'suggested' && (
              <div className="p-4">
                {suggestedConnections.length === 0 ? (
                  <p className="text-center text-gray-500 py-8">No suggested connections at this time</p>
                ) : (
                  <div className="space-y-3">
                    {suggestedConnections.map((user) => (
                      <div key={user.id} className="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg">
                        <div className="flex items-center space-x-3">
                          <div className="w-12 h-12 rounded-full bg-linkedin-blue flex items-center justify-center text-white font-semibold">
                            {user.name?.charAt(0)?.toUpperCase()}
                          </div>
                          <div>
                            <h3 className="font-semibold">{user.name}</h3>
                            <p className="text-sm text-gray-500">{user.email}</p>
                          </div>
                        </div>
                        <button
                          onClick={() => handleConnect(user.userId)}
                          className="flex items-center space-x-2 px-4 py-2 bg-linkedin-blue text-white rounded-full hover:bg-blue-700 transition"
                        >
                          <UserPlus size={18} />
                          <span>Connect</span>
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}
          </div>
        )}
      </div>
    </Layout>
  );
};

export default Connections;
