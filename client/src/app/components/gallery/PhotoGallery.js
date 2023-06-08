import React, { useState } from 'react';

const PhotoGallery = ({ images }) => {
  const [activeIndex, setActiveIndex] = useState(0);

  const handlePrev = () => {
    setActiveIndex((prevIndex) => (prevIndex === 0 ? images.length - 1 : prevIndex - 1));
  };

  const handleNext = () => {
    setActiveIndex((prevIndex) => (prevIndex === images.length - 1 ? 0 : prevIndex + 1));
  };

  return (
    <div className="small-photo-gallery">
      <img src={images[activeIndex]} alt="gallery" className="gallery-image" />

      <div className="gallery-thumbnails">
        {images.map((image, index) => (
          <img
            key={index}
            src={image}
            alt={`thumbnail ${index}`}
            className={`gallery-thumbnail ${index === activeIndex ? 'active' : ''}`}
            onClick={() => setActiveIndex(index)}
          />
        ))}
      </div>

      <button onClick={handlePrev} className="gallery-nav prev">
        &lt;
      </button>
      <button onClick={handleNext} className="gallery-nav next">
        &gt;
      </button>
    </div>
  );
}

export default PhotoGallery