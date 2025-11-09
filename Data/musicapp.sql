use musicapp
drop database musicapp
create database musicapp
INSERT INTO songs (
    `id`,
    `title`,
    `artist`,
    `duration`,
    `image_url`, -- 💡 Bao bọc bằng dấu nháy ngược
    `file_path`,
    `listen_count`,
    `like_count`,
    `description`,
    `created_at`,
    `label`
)
VALUES
('1', 'Chúng ta của hiện tại', 'Sơn Tùng M-TP', 200, 'https://i.pinimg.com/1200x/dc/e9/b5/dce9b590b593d06d7ab33fececde2017.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/SƠN TÙNG M-TP  CHÚNG TA CỦA HIỆN TẠI  OFFICIAL MUSIC VIDEO - Sơn Tùng M-TP Official.mp3', 0, 0, 'nhạc anh Tùng đẳng cấp', '2025-02-02', 'pop'),
('2', 'Âm thầm bên em', 'Sơn Tùng M-TP', 200, 'https://i.pinimg.com/736x/3e/1c/15/3e1c15d1c94692e375f883225975e790.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Âm Thầm Bên Em  OFFICIAL MUSIC VIDEO  Sơn Tùng M-TP - Sơn Tùng M-TP Official.mp3', 0, 0, 'nhạc anh Tùng đẳng cấp', '2025-02-02', 'pop'),
('3', 'Trap Queen', 'babysis', 180, 'https://i.pinimg.com/736x/6b/6d/97/6b6d97045deaa2db88de6e535a0f6ac2.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Trap Queen - Adriana Gomez  Eightfold X MKJ Remix (Lyrics + Vietsub) ♫ - Top Tik Tok.mp3', 0, 0, 'nhạc alime', '2025-02-02', 'nightcore'),
('4', 'Sakura', 'single mom', 190, 'https://i.pinimg.com/736x/8b/43/f2/8b43f2e7a607e6795d45e2ee9283b121.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Sakura Anata ni Deaete Yokatta - 5 centimet per second - Lyric Kara HD - Iloveokoloko.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02', 'japanese song'),
('5', 'NHAT', 'Phan Mạnh Quỳnh', 210, 'https://i.pinimg.com/736x/8e/a9/bb/8ea9bb20873d431f10e4a17573614f11.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/NHẠT - PHAN MẠNH QUỲNH [OFFICIAL MUSIC VIDEO] - Phan Mạnh Quỳnh Official.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02', 'pop'),
('6', 'Yêu 5', 'Jen Hoang', 230, 'https://i.pinimg.com/1200x/31/fb/38/31fb38281ee4dbb36c1da744f0966dad.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/NHẠT - PHAN MẠNH QUỲNH [OFFICIAL MUSIC VIDEO] - Phan Mạnh Quỳnh Official.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02', 'pop'),
('7', 'Khi phải quyên đi', 'Phan Mạnh Quỳnh', 220, 'https://i.pinimg.com/736x/ba/bf/d3/babfd323a2d3f4d926c8ccecced3f511.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Khi Phải Quên Đi  Phan Mạnh Quỳnh  Official Music Video - Phan Mạnh Quỳnh Official.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02', 'pop'),
('8', 'Có chàng trai viết lên cây', 'Phan Mạnh Quỳnh', 250, 'https://i.pinimg.com/736x/c5/4b/d4/c54bd4dfddf7a2454af43daf71789dcb.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Có Chàng Trai Viết Lên Cây - Phan Mạnh Quỳnh  AUDIO LYRIC OFFICIAL - Phan Mạnh Quỳnh Official.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02', 'pop'),
('9', 'Lay All Your Love On Me', 'EvanDrago', 260, 'https://i.pinimg.com/736x/9e/aa/1c/9eaa1c462d0b4c1e1bb2fdb24118f0dc.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/[Lyrics+Vietsub] Abba-Lay All Your Love On Me (Slowed+Reverb) - S P R I N G.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02', 'pop'),
('10', 'Dance Beat', 'DJ Mix', 270, 'https://i.pinimg.com/736x/66/b7/09/66b7097f77173017a43c5395df1c8d6f.jpg', '/home/tagiahuy/Documents/Final Project/CMCmp3_BE/Data/Music/Lyrics  YÊU 5 - Rhymastic - Jen Hoang.mp3', 0, 0, 'nhạc đẳng cấp', '2025-02-02', 'pop');

INSERT INTO `playlist` (
	`id`,
    `name`,
    `description`,
    `image_url`,
    `number_of_songs`,
    `created_at`,
    `listen_count`,
    `like_count`
) VALUES
('l1', 'Maybe you like', 'Popular songs', 'https://i.pinimg.com/736x/14/53/be/1453be3f6f3e5d02f65a67ef795d35a4.jpg', 110, '2025-02-02', 10, 10),
('l2', 'Recommended for you', 'My personal favorite songs', 'https://i.pinimg.com/736x/1d/a2/e9/1da2e931af8b657d985a01e54860b6c4.jpg', 5, '2025-02-02', 10, 10),
('l3', 'Top Hits', 'The most popular songs right now', 'https://i.pinimg.com/736x/6d/da/db/6ddadb2f383f1d05a74327fa6016bd7e.jpg', 4, '2025-02-02', 10, 10),
('l4', 'Chill Vibes', 'Relaxing and soothing tracks', 'https://i.pinimg.com/1200x/c9/1f/9a/c91f9aa538bc6445052aa031c56fc6dd.jpg', 2, '2025-02-02', 10, 10),
('l5', 'Workout Mix', 'High energy songs to keep you moving', 'https://i.pinimg.com/1200x/01/bb/48/01bb4808452e8e888ac12ca00e85945b.jpg', 2, '2025-02-02', 10, 10);

INSERT INTO `playlist_song` (`playlist_id`, `song_id`) VALUES
-- Playlist l1: Maybe you like (Tất cả 10 bài hát)
('l1', '1'),
('l1', '2'),
('l1', '3'),
('l1', '4'),
('l1', '5'),
('l1', '6'),
('l1', '7'),
('l1', '8'),
('l1', '9'),
('l1', '10'),

-- Playlist l2: Recommended for you (Bài 3, 4, 5, 6, 7)
('l2', '3'),
('l2', '4'),
('l2', '5'),
('l2', '6'),
('l2', '7'),

-- Playlist l3: Top Hits (Bài 5, 6, 9, 10)
('l3', '5'),
('l3', '6'),
('l3', '9'),
('l3', '10'),

-- Playlist l4: Chill Vibes (Bài 7, 8)
('l4', '7'),
('l4', '8'),

-- Playlist l5: Workout Mix (Bài 9, 10)
('l5', '9'),
('l5', '10');
