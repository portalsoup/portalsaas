CREATE TABLE if NOT EXISTS blog_post_route (
  blog_post_id INT,
  route_id INT,
  PRIMARY KEY (blog_post_id, route_id),
  CONSTRAINT fk_blog_post FOREIGN KEY(blog_post_id) REFERENCES blog_post(id),
  CONSTRAINT fk_route FOREIGN KEY(route_id) REFERENCES route(id)
);