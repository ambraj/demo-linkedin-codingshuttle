import { useState } from 'react';
import { ThumbsUp, MessageCircle } from 'lucide-react';
import { postsService } from '../services/postsService';
import toast from 'react-hot-toast';

const PostCard = ({ post }) => {
  const [liked, setLiked] = useState(false);
  const [likesCount, setLikesCount] = useState(post.likesCount || 0);

  const handleLike = async () => {
    try {
      if (liked) {
        // Unlike the post
        await postsService.unlikePost(post.id);
        setLiked(false);
        setLikesCount(prev => prev - 1);
      } else {
        // Like the post
        await postsService.likePost(post.id);
        setLiked(true);
        setLikesCount(prev => prev + 1);
      }
    } catch (error) {
      toast.error(`Failed to ${liked ? 'unlike' : 'like'} post`);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInHours = Math.floor((now - date) / (1000 * 60 * 60));
    
    if (diffInHours < 1) return 'Just now';
    if (diffInHours < 24) return `${diffInHours}h ago`;
    if (diffInHours < 168) return `${Math.floor(diffInHours / 24)}d ago`;
    return date.toLocaleDateString();
  };

  return (
    <div className="bg-white rounded-lg shadow mb-4 overflow-hidden">
      {/* Post Header */}
      <div className="p-4">
        <div className="flex items-start space-x-3">
          <div className="w-12 h-12 rounded-full bg-linkedin-blue flex items-center justify-center text-white font-semibold">
            {post.authorName?.charAt(0)?.toUpperCase() || 'U'}
          </div>
          <div className="flex-1">
            <h3 className="font-semibold text-gray-900">{post.authorName || 'Unknown User'}</h3>
            <p className="text-xs text-gray-500">{formatDate(post.createdAt)}</p>
          </div>
        </div>
      </div>

      {/* Post Content */}
      <div className="px-4 pb-3">
        <p className="text-gray-800 whitespace-pre-wrap">{post.content}</p>
      </div>

      {/* Post Stats */}
      {likesCount > 0 && (
        <div className="px-4 pb-2">
          <p className="text-sm text-gray-500">{likesCount} {likesCount === 1 ? 'like' : 'likes'}</p>
        </div>
      )}

      {/* Post Actions */}
      <div className="border-t border-gray-200 px-4 py-2 flex items-center justify-around">
        <button
          onClick={handleLike}
          className={`flex items-center space-x-2 px-4 py-2 rounded-lg transition ${
            liked 
              ? 'text-linkedin-blue bg-blue-50' 
              : 'text-gray-600 hover:bg-gray-100'
          }`}
        >
          <ThumbsUp size={20} fill={liked ? 'currentColor' : 'none'} />
          <span className="font-medium">Like</span>
        </button>
        <button className="flex items-center space-x-2 px-4 py-2 rounded-lg text-gray-600 hover:bg-gray-100 transition">
          <MessageCircle size={20} />
          <span className="font-medium">Comment</span>
        </button>
      </div>
    </div>
  );
};

export default PostCard;
