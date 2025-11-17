DROP DATABASE IF EXISTS musicapp;
CREATE DATABASE musicapp;
USE musicapp;


TRUNCATE TABLE playlists;
INSERT INTO playlists (id, title, description, image_url, play_count, like_count, created_at, owner_id) VALUES (1, 'Maybe you like', 'Popular songs', 'https://i.pinimg.com/736x/14/53/be/1453be3f6f3e5d02f65a67ef795d35a4.jpg', 10, 10, '2025-02-02 00:00:00', 2), (2, 'Recommended for you', 'My personal favorite songs', 'https://i.pinimg.com/736x/1d/a2/e9/1da2e931af8b657d985a01e54860b6c4.jpg', 10, 10, '2025-02-02 00:00:00', 2), (3, 'Top Hits', 'The most popular songs right now', 'https://i.pinimg.com/736x/6d/da/db/6ddadb2f383f1d05a74327fa6016bd7e.jpg', 10, 10, '2025-02-02 00:00:00', 2), (4, 'Chill Vibes', 'Relaxing and soothing tracks', 'https://i.pinimg.com/1200x/c9/1f/9a/c91f9aa538bc6445052aa031c56fc6dd.jpg', 10, 10, '2025-02-02 00:00:00', 2), (5, 'Workout Mix', 'High energy songs to keep you moving', 'https://i.pinimg.com/1200x/01/bb/48/01bb4808452e8e888ac12ca00e85945b.jpg', 10, 10, '2025-02-02 00:00:00', 2);

INSERT INTO artists (id, name, image_url, song_count) VALUES
(1, 'Sơn Tùng M-TP', NULL, 2),
(2, 'babysis', NULL, 1),
(3, 'single mom', NULL, 1),
(4, 'Phan Mạnh Quỳnh', NULL, 3),
(5, 'Jen Hoang', NULL, 1),
(6, 'EvanDrago', NULL, 1),
(7, 'DJ Mix', NULL, 1);


INSERT INTO tags (id, name, description) VALUES
(1, 'V-Pop', 'Nhạc Pop Việt Nam'),
(2, 'Rap Việt', 'Nhạc Rap/Hip-hop Việt Nam'),
(3, 'US-UK', 'Nhạc Pop Âu Mỹ'),
(4, 'K-Pop', 'Nhạc Pop Hàn Quốc');


SET FOREIGN_KEY_CHECKS = 0;
SET FOREIGN_KEY_CHECKS = 1;


INSERT INTO songs (id, title, duration, image_url, file_path, listen_count, like_count, description, created_at) VALUES
(1, 'Chúng ta của hiện tại', 200, 'https://i.pinimg.com/1200x/dc/e9/b5/dce9b590b593d06d7ab33fececde2017.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/SƠN TÙNG M-TP  CHÚNG TA CỦA HIỆN TẠI  OFFICIAL MUSIC VIDEO - Sơn Tùng M-TP Official.mp3', 0, 0, 'nhạc anh Tùng đẳng cấp', '2025-02-02 00:00:00'),
(2, 'Âm thầm bên em', 200, 'https://i.pinimg.com/736x/3e/1c/15/3e1c15d1c94692e375f883225975e790.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Âm Thầm Bên Em  OFFICIAL MUSIC VIDEO  Sơn Tùng M-TP - Sơn Tùng M-TP Official.mp3', 0, 0, 'nhạc anh Tùng đẳng cấp', '2025-02-02 00:00:00'),
(3, 'Trap Queen', 180, 'https://i.pinimg.com/736x/6b/6d/97/6b6d97045deaa2db88de6e535a0f6ac2.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Trap Queen - Adriana Gomez  Eightfold X MKJ Remix (Lyrics + Vietsub) ♫ - Top Tik Tok.mp3', 0, 0, 'nhạc alime', '2025-02-02 00:00:00'),
(4, 'Sakura', 190, 'https://i.pinimg.com/736x/8b/43/f2/8b43f2e7a607e6795d45e2ee9283b121.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Sakura Anata ni Deaete Yokatta - 5 centimet per second - Lyric Kara HD - Iloveokoloko.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02 00:00:00'),
(5, 'NHAT', 210, 'https://i.pinimg.com/736x/8e/a9/bb/8ea9bb20873d431f10e4a17573614f11.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/NHẠT - PHAN MẠNH QUỲNH [OFFICIAL MUSIC VIDEO] - Phan Mạnh Quỳnh Official.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02 00:00:00'),
(6, 'Yêu 5', 230, 'https://i.pinimg.com/1200x/31/fb/38/31fb38281ee4dbb36c1da744f0966dad.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/NHẠT - PHAN MẠNH QUỲNH [OFFICIAL MUSIC VIDEO] - Phan Mạnh Quỳnh Official.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02 00:00:00'),
(7, 'Khi phải quyên đi', 220, 'https://i.pinimg.com/736x/ba/bf/d3/babfd323a2d3f4d926c8ccecced3f511.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Khi Phải Quên Đi  Phan Mạnh Quỳnh  Official Music Video - Phan Mạnh Quỳnh Official.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02 00:00:00'),
(8, 'Có chàng trai viết lên cây', 250, 'https://i.pinimg.com/736x/c5/4b/d4/c54bd4dfddf7a2454af43daf71789dcb.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Có Chàng Trai Viết Lên Cây - Phan Mạnh Quỳnh  AUDIO LYRIC OFFICIAL - Phan Mạnh Quỳnh Official.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02 00:00:00'),
(9, 'Lay All Your Love On Me', 260, 'https://i.pinimg.com/736x/9e/aa/1c/9eaa1c462d0b4c1e1bb2fdb24118f0dc.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/[Lyrics+Vietsub] Abba-Lay All Your Love On Me (Slowed+Reverb) - S P R I N G.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02 00:00:00'),
(10, 'Dance Beat', 270, 'https://i.pinimg.com/736x/66/b7/09/66b7097f77173017a43c5395df1c8d6f.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Lyrics YÊU 5 - Rhymastic - Jen Hoang.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02 00:00:00');


INSERT INTO song_artists (song_id, artist_id) VALUES
(1, 1),  -- Chúng ta của hiện tại - Sơn Tùng M-TP
(2, 1),  -- Âm thầm bên em - Sơn Tùng M-TP
(3, 2),  -- Trap Queen - babysis
(4, 3),  -- Sakura - single mom
(5, 4),  -- NHAT - Phan Mạnh Quỳnh
(6, 5),  -- Yêu 5 - Jen Hoang
(7, 4),  -- Khi phải quyên đi - Phan Mạnh Quỳnh
(8, 4),  -- Có chàng trai viết lên cây - Phan Mạnh Quỳnh
(9, 6),  -- Lay All Your Love On Me - EvanDrago
(10, 7); -- Dance Beat - DJ Mix


INSERT INTO song_tags (song_id, tag_id) VALUES
(1, 1),  -- pop
(2, 1),  -- pop
(3, 2),  -- nightcore
(4, 3),  -- japanese song
(5, 1),  -- pop
(6, 1),  -- pop
(7, 1),  -- pop
(8, 1),  -- pop
(9, 1),  -- pop
(10, 1); -- pop



INSERT INTO playlist_songs (playlist_id, song_id, song_order, added_at) VALUES
-- Playlist 1: Maybe you like (Tất cả 10 bài hát)
(1, 1, 1, NOW()),
(1, 2, 2, NOW()),
(1, 3, 3, NOW()),
(1, 4, 4, NOW()),
(1, 5, 5, NOW()),
(1, 6, 6, NOW()),
(1, 7, 7, NOW()),
(1, 8, 8, NOW()),
(1, 9, 9, NOW()),
(1, 10, 10, NOW()),
-- Playlist 2: Recommended for you (Bài 3, 4, 5, 6, 7)
(2, 3, 1, NOW()),
(2, 4, 2, NOW()),
(2, 5, 3, NOW()),
(2, 6, 4, NOW()),
(2, 7, 5, NOW()),
-- Playlist 3: Top Hits (Bài 5, 6, 9, 10)
(3, 5, 1, NOW()),
(3, 6, 2, NOW()),
(3, 9, 3, NOW()),
(3, 10, 4, NOW()),
-- Playlist 4: Chill Vibes (Bài 7, 8)
(4, 7, 1, NOW()),
(4, 8, 2, NOW()),
-- Playlist 5: Workout Mix (Bài 9, 10)
(5, 9, 1, NOW()),
(5, 10, 2, NOW());

























