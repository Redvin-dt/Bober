package ru.hse.client.chapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.hse.server.proto.EntitiesProto
import java.util.concurrent.atomic.AtomicReference

class ChapterCreatePageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    activity: ChapterCreateActivity,
    testStartPositions: MutableList<Int>,
    testListBuilder: EntitiesProto.TestList.Builder,
    chapterTestsCreationFragmentRef: AtomicReference<ChapterTestsCreationFragment?>,
    chapterSettingsFragmentRef: AtomicReference<ChapterSettingsFragment?>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private var mActivity = activity
    private var mTestListBuilder:  EntitiesProto.TestList.Builder = testListBuilder
    private var mTestStartPositions: MutableList<Int> = testStartPositions
    private var mChapterTestsCreationFragmentRef:  AtomicReference<ChapterTestsCreationFragment?> = chapterTestsCreationFragmentRef
    private val mChapterSettingsFragment: AtomicReference<ChapterSettingsFragment?> = chapterSettingsFragmentRef
    private lateinit var chapterTestsCreationFragment: ChapterTestsCreationFragment
    private lateinit var chapterSettingsFragment: ChapterSettingsFragment


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                chapterTestsCreationFragment = ChapterTestsCreationFragment(mActivity, mTestStartPositions, mTestListBuilder)
                mChapterTestsCreationFragmentRef.set(chapterTestsCreationFragment)
                chapterTestsCreationFragment
            }
            else -> {
                chapterSettingsFragment = ChapterSettingsFragment()
                mChapterSettingsFragment.set(chapterSettingsFragment)
                chapterSettingsFragment
            }
        }
    }
}