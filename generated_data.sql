
-- Generated data for musicapp

-- Users
INSERT INTO `users` (`id`, `username`, `display_name`, `email`, `password`, `role`, `status`, `provider`, `created_at`, `updated_at`) VALUES
(1, 'admin', 'Admin User', 'admin@example.com', '$2a$10$e.4s0s5j5k3l2k1j1h2g3f4d5s6a7w8e9d.e.f.g.h.i.j.k.l', 'ADMIN', 'ACTIVE', 'LOCAL', NOW(), NOW()),
(2, 'user1', 'Normal User 1', 'user1@example.com', '$2a$10$e.4s0s5j5k3l2k1j1h2g3f4d5s6a7w8e9d.e.f.g.h.i.j.k.l', 'USER', 'ACTIVE', 'LOCAL', NOW(), NOW()),
(3, 'user2', 'Normal User 2', 'user2@example.com', '$2a$10$e.4s0s5j5k3l2k1j1h2g3f4d5s6a7w8e9d.e.f.g.h.i.j.k.l', 'USER', 'ACTIVE', 'LOCAL', NOW(), NOW());

-- Artists
INSERT INTO `artists` (`id`, `name`, `image_url`, `song_count`) VALUES
(1, 'Sơn Tùng M-TP', 'Data/Image/sontung.jpg', 2),
(2, 'Phan Mạnh Quỳnh', 'Data/Image/phanmanhquynh.jpg', 3),
(3, 'Rhymastic', NULL, 1),
(4, 'Adriana Gomez', NULL, 1),
(5, 'S P R I N G', NULL, 1),
(6, 'Iloveokoloko', NULL, 1);


-- Tags
INSERT INTO `tags` (`id`, `name`, `description`) VALUES
(1, 'V-Pop', 'Vietnamese Pop Music'),
(2, 'Ballad', 'Slow and emotional songs'),
(3, 'Pop', 'Popular music'),
(4, 'Remix', 'Remixed songs'),
(5, 'Anime', 'Songs from Anime');

-- Songs
INSERT INTO `songs` (`id`, `title`, `duration`, `file_path`, `image_url`, `listen_count`, `like_count`, `description`, `created_at`, `updated_at`, `uploader_id`) VALUES
(1, 'Chúng Ta Của Hiện Tại', 305, 'Data/Music/SƠN TÙNG M-TP  CHÚNG TA CỦA HIỆN TẠI  OFFICIAL MUSIC VIDEO - Sơn Tùng M-TP Official.mp3', 'Data/Image/sontung2.jpg', 100, 20, 'Một bài hát của Sơn Tùng M-TP', NOW(), NOW(), 1),
(2, 'Âm Thầm Bên Em', 289, 'Data/Music/Âm Thầm Bên Em  OFFICIAL MUSIC VIDEO  Sơn Tùng M-TP - Sơn Tùng M-TP Official.mp3', 'Data/Image/sontung.jpg', 150, 30, 'Một bài hát khác của Sơn Tùng M-TP', NOW(), NOW(), 1),
(3, 'Có Chàng Trai Viết Lên Cây', 310, 'Data/Music/Có Chàng Trai Viết Lên Cây - Phan Mạnh Quỳnh  AUDIO LYRIC OFFICIAL - Phan Mạnh Quỳnh Official.mp3', 'Data/Image/phanmanhquynh.jpg', 200, 50, 'Một bài hát của Phan Mạnh Quỳnh', NOW(), NOW(), 2),
(4, 'Khi Phải Quên Đi', 270, 'Data/Music/Khi Phải Quên Đi  Phan Mạnh Quỳnh  Official Music Video - Phan Mạnh Quỳnh Official.mp3', 'Data/Image/phanmanhquynh2.jpg', 120, 25, 'Một bài hát khác của Phan Mạnh Quỳnh', NOW(), NOW(), 2),
(5, 'NHẠT', 324, 'Data/Music/NHẠT - PHAN MẠNH QUỲNH [OFFICIAL MUSIC VIDEO] - Phan Mạnh Quỳnh Official.mp3', 'Data/Image/phanmanhquynh.jpg', 80, 15, 'Một bài hát nữa của Phan Mạnh Quỳnh', NOW(), NOW(), 2),
(6, 'YÊU 5', 222, 'Data/Music/Lyrics  YÊU 5 - Rhymastic - Jen Hoang.mp3', 'Data/Image/agirl.jpg', 300, 80, 'Một bài hát của Rhymastic', NOW(), NOW(), 1),
(7, 'Trap Queen (Remix)', 211, 'Data/Music/Trap Queen - Adriana Gomez  Eightfold X MKJ Remix (Lyrics + Vietsub) ♫ - Top Tik Tok.mp3', 'Data/Image/agirl2.jpg', 500, 120, 'Bản remix Trap Queen', NOW(), NOW(), 3),
(8, 'Lay All Your Love On Me (Slowed+Reverb)', 180, 'Data/Music/[Lyrics+Vietsub] Abba-Lay All Your Love On Me (Slowed+Reverb) - S P R I N G.mp3', 'Data/Image/girl.jpg', 250, 60, 'Bản slowed and reverb', NOW(), NOW(), 3),
(9, 'Sakura Anata ni Deaete Yokatta', 336, 'Data/Music/Sakura Anata ni Deaete Yokatta - 5 centimet per second - Lyric Kara HD - Iloveokoloko.mp3', 'Data/Image/boy.jpg', 400, 100, 'Nhạc phim 5cm/s', NOW(), NOW(), 1);

-- song_artists
INSERT INTO `song_artists` (`song_id`, `artist_id`) VALUES
(1, 1),
(2, 1),
(3, 2),
(4, 2),
(5, 2),
(6, 3),
(7, 4),
(8, 5),
(9, 6);

-- song_tags
INSERT INTO `song_tags` (`song_id`, `tag_id`) VALUES
(1, 1), (1, 3),
(2, 1), (2, 2),
(3, 1), (3, 2),
(4, 1), (4, 2),
(5, 1),
(6, 1), (6, 3),
(7, 4),
(8, 3), (8, 4),
(9, 5);

-- Playlists
INSERT INTO `playlists` (`id`, `title`, `description`, `image_url`, `play_count`, `like_count`, `comment_count`, `created_at`, `owner_id`) VALUES
(1, 'V-Pop Hits', 'Những bài hát V-Pop hay nhất', 'Data/Image/denvau.jpg', 0, 0, 0, NOW(), 1),
(2, 'Ballad Buồn', 'Tuyển tập những bản ballad buồn', 'Data/Image/denvau2.jpg', 0, 0, 0, NOW(), 2),
(3, 'Remix Sôi Động', 'Nhạc remix cho những bữa tiệc', 'Data/Image/florentino.jpg', 0, 0, 0, NOW(), 3);

-- playlist_songs
INSERT INTO `playlist_songs` (`playlist_id`, `song_id`, `song_order`, `added_at`) VALUES
-- V-Pop Hits
(1, 1, 1, NOW()),
(1, 2, 2, NOW()),
(1, 3, 3, NOW()),
(1, 6, 4, NOW()),
-- Ballad Buồn
(2, 2, 1, NOW()),
(2, 3, 2, NOW()),
(2, 4, 3, NOW()),
-- Remix Sôi Động
(3, 7, 1, NOW()),
(3, 8, 2, NOW());

-- song_likes
INSERT INTO `song_likes` (`user_id`, `song_id`, `liked_at`) VALUES
(1, 1, NOW()),
(1, 3, NOW()),
(2, 1, NOW()),
(2, 4, NOW()),
(3, 5, NOW()),
(3, 6, NOW()),
(3, 7, NOW());

-- playlist_likes
INSERT INTO `playlist_likes` (`user_id`, `playlist_id`, `created_at`) VALUES
(1, 2, NOW()),
(2, 1, NOW()),
(3, 1, NOW()),
(3, 3, NOW());

-- playlist_comments
INSERT INTO `playlist_comments` (`id`, `content`, `user_id`, `playlist_id`, `created_at`) VALUES
(1, 'Playlist này hay quá!', 2, 1, NOW()),
(2, 'Toàn bài mình thích', 3, 1, NOW()),
(3, 'Nghe buồn não nề', 1, 2, NOW());
